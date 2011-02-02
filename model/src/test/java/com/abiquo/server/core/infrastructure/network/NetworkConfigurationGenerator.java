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
 * Boston, MA 02111-1307, USA. */
package com.abiquo.server.core.infrastructure.network;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class NetworkConfigurationGenerator extends DefaultEntityGenerator<NetworkConfiguration>
{
    DhcpGenerator dhcpGenerator;

    public NetworkConfigurationGenerator(SeedGenerator seed)
    {
        super(seed);
        dhcpGenerator = new DhcpGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(NetworkConfiguration obj1, NetworkConfiguration obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, NetworkConfiguration.ID_PROPERTY);
    }

    @Override
    public NetworkConfiguration createUniqueInstance()
    {
        int seed = nextSeed();

        String address = "192.168.1.0";
        String gateway = "192.168.1.1";
        String primaryDNS = "8.8.8.8";
        String secondaryDNS = "9.9.9.9";

        String fenceMode =
            newString(seed, NetworkConfiguration.FENCE_MODE_LENGTH_MIN,
                NetworkConfiguration.FENCE_MODE_LENGTH_MAX);

        String netmask =
            newString(seed, NetworkConfiguration.NETMASK_LENGTH_MIN,
                NetworkConfiguration.NETMASK_LENGTH_MAX);

        Integer mask = nextSeed();

        NetworkConfiguration config = new NetworkConfiguration(address, mask, netmask, fenceMode);
        config.setDhcp(dhcpGenerator.createUniqueInstance());
        config.setGateway(gateway);
        config.setPrimaryDNS(primaryDNS);
        config.setSecondaryDNS(secondaryDNS);

        return config;
    }
    
    /**
     * Create a new configuration from an already created RemoteService.
     * @param dhcpService dhcp remote service already created.
     * @return the generated {@link NetworkConfiguration} object
     */
    public NetworkConfiguration createInstance(RemoteService dhcpService)
    {
        int seed = nextSeed();

        String address = "192.168.1.0";
        String gateway = "192.168.1.1";
        String primaryDNS = "8.8.8.8";
        String secondaryDNS = "9.9.9.9";

        String fenceMode =
            newString(seed, NetworkConfiguration.FENCE_MODE_LENGTH_MIN,
                NetworkConfiguration.FENCE_MODE_LENGTH_MAX);

        String netmask =
            newString(seed, NetworkConfiguration.NETMASK_LENGTH_MIN,
                NetworkConfiguration.NETMASK_LENGTH_MAX);

        Integer mask = nextSeed();

        NetworkConfiguration config = new NetworkConfiguration(address, mask, netmask, fenceMode);
        config.setDhcp(dhcpGenerator.createInstance(dhcpService));
        config.setGateway(gateway);
        config.setPrimaryDNS(primaryDNS);
        config.setSecondaryDNS(secondaryDNS);
        
        return config;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(NetworkConfiguration entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        dhcpGenerator.addAuxiliaryEntitiesToPersist(entity.getDhcp(), entitiesToPersist);
        entitiesToPersist.add(entity.getDhcp());
    }
    
    public NetworkConfiguration createInstance(RemoteService dhcpService, String netmask)
    {
        int seed = nextSeed();
        
        String address = "192.168.1.0";
        String gateway = "192.168.1.1";
        String primaryDNS = "8.8.8.8";
        String secondaryDNS = "9.9.9.9";
        
        String fenceMode =
            newString(seed, NetworkConfiguration.FENCE_MODE_LENGTH_MIN,
                NetworkConfiguration.FENCE_MODE_LENGTH_MAX);
        
        Integer mask = IPNetworkRang.transformIPMaskToIntegerMask(IPAddress.newIPAddress(netmask));

        NetworkConfiguration config = new NetworkConfiguration(address, mask, netmask, fenceMode);
        config.setDhcp(dhcpGenerator.createInstance(dhcpService));
        config.setGateway(gateway);
        config.setPrimaryDNS(primaryDNS);
        config.setSecondaryDNS(secondaryDNS);
        
        return config;        
        
    }
}
