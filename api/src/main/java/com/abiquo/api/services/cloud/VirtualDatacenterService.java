/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.api.services.cloud;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.UserService;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.enterprise.DatacenterLimitsDAO;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterRep;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.Dhcp;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.NetworkConfigurationDto;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;

@Service
@Transactional(readOnly = true)
public class VirtualDatacenterService extends DefaultApiService
{

    public static final String FENCE_MODE = "bridge";

    @Autowired
    VirtualDatacenterRep repo;

    @Autowired
    DatacenterRep datacenterRepo;

    @Autowired
    UserService userService;

    @Autowired
    DatacenterLimitsDAO datacenterLimitsDao;

    public VirtualDatacenterService()
    {

    }

    // use this to initialize it for tests
    public VirtualDatacenterService(EntityManager em)
    {
        repo = new VirtualDatacenterRep(em);
        datacenterRepo = new DatacenterRep(em);
        datacenterLimitsDao = new DatacenterLimitsDAO(em);
    }

    public Collection<VirtualDatacenter> getVirtualDatacenters(Enterprise enterprise,
        Datacenter datacenter)
    {
        User user = userService.getCurrentUser();
        return getVirtualDatacenters(enterprise, datacenter, user);
    }

    Collection<VirtualDatacenter> getVirtualDatacenters(Enterprise enterprise,
        Datacenter datacenter, User user)
    {
        boolean findByUser =
            user != null
                && (user.getRole().getType() == Role.Type.USER && !StringUtils.isEmpty(user
                    .getAvailableVirtualDatacenters()));

        if (enterprise == null && user != null)
        {
            enterprise = user.getEnterprise();
        }

        if (findByUser)
        {
            return repo.findByEnterpriseAndDatacenter(enterprise, datacenter, user);
        }
        else
        {
            return repo.findByEnterpriseAndDatacenter(enterprise, datacenter);
        }
    }

    public VirtualDatacenter getVirtualDatacenter(Integer id)
    {
        VirtualDatacenter vdc = repo.findById(id);
        if (vdc == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
        }
        return vdc;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualDatacenter createVirtualDatacenter(VirtualDatacenterDto dto,
        Datacenter datacenter, Enterprise enterprise)
    {
        if (!isValidEnterpriseDatacenter(enterprise, datacenter))
        {
            errors.add(APIError.DATACENTER_NOT_ALLOWD);
            flushErrors();
        }

        Network network = createNetwork();

        NetworkConfigurationDto config = dto.getNetworkConfiguration();

        VirtualDatacenter vdc = createVirtualDatacenter(dto, datacenter, enterprise, network);

        NetworkConfiguration networkConfiguration = createNetworkConfiguration(config);

        VLANNetwork vlan = createVlan(network, config, networkConfiguration);

        Collection<IPAddress> addressRange = calculateIPRange(config);

        createDhcp(datacenter, vdc, vlan, networkConfiguration, addressRange);

        return vdc;
    }

    protected boolean isValidEnterpriseDatacenter(Enterprise enterprise, Datacenter datacenter)
    {
        return true;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualDatacenter updateVirtualDatacenter(Integer id, VirtualDatacenterDto dto)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(id);

        return updateVirtualDatacenter(vdc, dto);
    }

    protected VirtualDatacenter updateVirtualDatacenter(VirtualDatacenter vdc,
        VirtualDatacenterDto dto)
    {
        vdc.setName(dto.getName());
        setLimits(dto, vdc);

        if (!vdc.isValid())
        {
            validationErrors.addAll(vdc.getValidationErrors());
            flushErrors();
        }
        if (!isValidVlanHardLimitPerVdc(vdc.getVlanHard()))
        {
            errors.add(APIError.LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC);
            flushErrors();
        }

        repo.update(vdc);

        return vdc;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualDatacenter(Integer id)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(id);

        if (repo.containsVirtualAppliances(vdc))
        {
            errors.add(APIError.VIRTUAL_DATACENTER_CONTAINS_VIRTUAL_APPLIANCES);
        }

        if (repo.containsResources(vdc, "8")) // id 8 -> Volumes. FIXME: add enumeration
        {
            errors.add(APIError.VIRTUAL_DATACENTER_CONTAINS_RESOURCES);
        }

        flushErrors();

        repo.delete(vdc);
    }

    private Network createNetwork()
    {
        Network network = new Network(UUID.randomUUID().toString());
        repo.insertNetwork(network);
        return network;
    }

    private VLANNetwork createVlan(Network network, NetworkConfigurationDto config,
        NetworkConfiguration networkConfiguration)
    {
        VLANNetwork vlan =
            new VLANNetwork(config.getNetworkName(), network, 1, networkConfiguration);
        repo.insertVlan(vlan);
        return vlan;
    }

    private VirtualDatacenter createVirtualDatacenter(VirtualDatacenterDto dto,
        Datacenter datacenter, Enterprise enterprise, Network network)
    {
        VirtualDatacenter vdc =
            new VirtualDatacenter(enterprise, datacenter, network, dto.getHypervisorType(), dto
                .getName());

        setLimits(dto, vdc);
        validateVirtualDatacenter(vdc, dto.getNetworkConfiguration(), datacenter);

        repo.insert(vdc);
        return vdc;
    }

    private void setLimits(VirtualDatacenterDto dto, VirtualDatacenter vdc)
    {
        vdc.setCpuCountLimits(new Limit((long) dto.getCpuCountSoftLimit(), (long) dto
            .getCpuCountHardLimit()));
        vdc.setHdLimitsInMb(new Limit(dto.getHdSoftLimitInMb(), dto.getHdHardLimitInMb()));
        vdc.setRamLimitsInMb(new Limit((long) dto.getRamSoftLimitInMb(), (long) dto
            .getRamHardLimitInMb()));
        vdc.setStorageLimits(new Limit(dto.getStorageSoft(), dto.getStorageHard()));
        vdc.setVlansLimits(new Limit(dto.getVlansSoft(), dto.getVlansHard()));
        vdc.setPublicIPLimits(new Limit(dto.getPublicIpsSoft(), dto.getPublicIpsHard()));
    }

    private void validateVirtualDatacenter(VirtualDatacenter vdc, NetworkConfigurationDto config,
        Datacenter datacenter)
    {
        if (config == null)
        {
            errors.add(APIError.NETWORK_INVALID_CONFIGURATION);
        }

        if (!vdc.isValid())
        {
            validationErrors.addAll(vdc.getValidationErrors());
        }
        else if (!isValidVlanHardLimitPerVdc(vdc.getVlanHard()))
        {
            errors.add(APIError.LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC);
        }

        if (vdc.getHypervisorType() != null
            && !isValidHypervisorForDatacenter(vdc.getHypervisorType(), datacenter))
        {
            errors.add(APIError.VIRTUAL_DATACENTER_INVALID_HYPERVISOR_TYPE);
        }

        flushErrors();
    }

    private boolean isValidVlanHardLimitPerVdc(long vlansHard)
    {
        String limitS = System.getProperty("abiquo.server.networking.vlanPerVdc", "0");
        int limit = Integer.valueOf(limitS);

        return limit == 0 || limit >= vlansHard;
    }

    private boolean isValidHypervisorForDatacenter(HypervisorType type, Datacenter datacenter)
    {
        return datacenterRepo.findHypervisors(datacenter).contains(type);
    }

    private Collection<IPAddress> calculateIPRange(NetworkConfigurationDto networkConfiguration)
    {
        Collection<IPAddress> range =
            IPNetworkRang.calculateWholeRange(IPAddress.newIPAddress(networkConfiguration
                .getAddress()), networkConfiguration.getMask());

        if (!IPAddress.isIntoRange(range, networkConfiguration.getGateway()))
        {
            errors.add(APIError.NETWORK_GATEWAY_OUT_OF_RANGE);
            flushErrors();
        }

        return range;
    }

    private NetworkConfiguration createNetworkConfiguration(NetworkConfigurationDto dto)
    {
        NetworkConfiguration config =
            new NetworkConfiguration(dto.getAddress(), dto.getMask(), dto.getNetMask(), FENCE_MODE);
        config.setGateway(dto.getGateway());
        config.setPrimaryDNS(dto.getPrimaryDNS());
        config.setSecondaryDNS(dto.getSecondaryDNS());
        config.setSufixDNS(dto.getSufixDNS());

        if (!config.isValid())
        {
            validationErrors.addAll(config.getValidationErrors());
            flushErrors();
        }

        repo.insertNetworkConfig(config);

        return config;
    }

    private Dhcp createDhcp(Datacenter datacenter, VirtualDatacenter vdc, VLANNetwork vlan,
        NetworkConfiguration networkConfiguration, Collection<IPAddress> range)
    {
        List<RemoteService> dhcpServiceList =
            datacenterRepo.findRemoteServiceWithTypeInDatacenter(datacenter,
                RemoteServiceType.DHCP_SERVICE);
        Dhcp dhcp = new Dhcp();

        if (!dhcpServiceList.isEmpty())
        {
            RemoteService dhcpService = dhcpServiceList.get(0);
            dhcp = new Dhcp(dhcpService);
        }

        repo.insertDhcp(dhcp);

        Collection<String> allMacAddresses = repo.getAllMacs();

        for (IPAddress address : range)
        {
            String macAddress = null;
            do
            {
                macAddress = IPNetworkRang.requestRandomMacAddress(vdc.getHypervisorType());
            }
            while (allMacAddresses.contains(macAddress));
            allMacAddresses.add(macAddress);

            String name = macAddress + "_host";

            IpPoolManagement ipManagement =
                new IpPoolManagement(dhcp, vlan, macAddress, name, address.toString(), vlan
                    .getName());

            ipManagement.setVirtualDatacenter(vdc);

            repo.insertIpManagement(ipManagement);
        }

        networkConfiguration.setDhcp(dhcp);

        return dhcp;
    }
}
