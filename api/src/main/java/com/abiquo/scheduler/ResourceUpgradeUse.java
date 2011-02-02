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

package com.abiquo.scheduler;

import java.io.StringReader;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.wink.common.internal.providers.entity.csv.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abiquo.scheduler.workload.NotEnoughResourcesException;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDAO;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDAO;
import com.abiquo.server.core.infrastructure.DatacenterRep;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDAO;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdDAO;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDAO;
import com.abiquo.server.core.infrastructure.network.NetworkAssignment;
import com.abiquo.server.core.infrastructure.network.NetworkAssignmentDAO;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDAO;

/**
 * Updates the following resource.
 * <ul>
 * <li>hardware resource (ram, cpu) on the physical machine</li>
 * <li>datastore utilization (hd) on the physical machine.</li>
 * <li>network resources on the virtual datacenter</li>
 * </ul>
 */
@Component
// @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation =
// Isolation.READ_UNCOMMITTED)
// @Transactional//(readOnly=false)
public class ResourceUpgradeUse implements IResourceUpgradeUse
{

    @Autowired
    DatacenterRep datacenterRepo;

    @Autowired
    DatastoreDAO datastoreDao;

    @Autowired
    VirtualApplianceDAO virtualApplianceDao;

    @Autowired
    NetworkAssignmentDAO netAssignDao;

    @Autowired
    IpPoolManagementDAO ipPoolManDao;

    @Autowired
    VLANNetworkDAO vlanNetworkDao;

    @Autowired
    RasdDAO rasdDao;

    @Autowired
    VirtualMachineDAO vmachineDao;

    private final static Logger log = LoggerFactory.getLogger(ResourceUpgradeUse.class);

    /**
     * @throws ResourceUpgradeUseException, if the operation can be performed: there isn't enough
     *             resources to allocate the virtual machine, the virtual appliances is not on any
     *             virtual datacenter.
     */
    @Override
    public void updateUse(final Integer virtualApplianceId, VirtualMachine virtualMachine)
        throws ResourceUpgradeUseException
    {

        if (virtualMachine.getHypervisor() == null
            || virtualMachine.getHypervisor().getMachine() == null)
        {
            throw new ResourceUpgradeUseException("Virtual machine is not allocated on any hypervisor / machine");
        }

        Machine physicalMachine = virtualMachine.getHypervisor().getMachine();

        try
        {
            updateUsageDatastore(virtualMachine, false);

            updateUsagePhysicalMachine(physicalMachine, virtualMachine, false);

            updateNetworkingResources(physicalMachine, virtualMachine, virtualApplianceId);

            virtualMachine.setState(State.IN_PROGRESS);
            vmachineDao.flush();
        }
        catch (final Exception e) // HibernateException NotEnoughResourcesException
        // NoSuchObjectException
        {
            e.printStackTrace(); // FIXME
            throw new ResourceUpgradeUseException(e);
        }
    }

    @Override
    public void rollbackUse(final VirtualMachine virtualMachine)
    {

        final Machine physicalMachine = virtualMachine.getHypervisor().getMachine();

        try
        {
            updateUsageDatastore(virtualMachine, true);

            updateUsagePhysicalMachine(physicalMachine, virtualMachine, true);

            rollbackNetworkingResources(physicalMachine, virtualMachine);

            virtualMachine.setState(State.NOT_DEPLOYED);
            vmachineDao.flush();
        }
        catch (final Exception e) // HibernateException NotEnoughResourcesException
        // NoSuchObjectException
        {
            throw new ResourceUpgradeUseException(e);
        }
    }

    /**
     * Updates the networking resources
     * 
     * @param session the session
     * @param virtualMachine the virtual machine
     * @param physicalTarget the physical target
     * @param virtualApplianceId
     * @throws NotEnoughResourcesException
     * @throws NoSuchObjectException
     */
    private void updateNetworkingResources(final Machine physicalTarget,
        final VirtualMachine virtualMachine, final int virtualApplianceId)
        throws NotEnoughResourcesException, NoSuchObjectException
    {
        final VirtualAppliance vapp = virtualApplianceDao.findById(virtualApplianceId);

        final VirtualDatacenter virtualDatacenter = vapp.getVirtualDatacenter();

        final List<NetworkAssignment> networksAssignedList =
            netAssignDao.findByVirtualDatacenter(virtualDatacenter);

        List<IpPoolManagement> ippoolManagementList =
            ipPoolManDao.findByVirtualMachine(virtualMachine.getId());

        if (networksAssignedList.isEmpty())
        {
            log.debug("The virtual machine has no network with a rack assigned.");
            for (final IpPoolManagement ipPoolManagement : ippoolManagementList)
            {
                Rack rack = physicalTarget.getRack();
                VLANNetwork vlanNetwork = ipPoolManagement.getVlanNetwork();
                final NetworkAssignment nb =
                    new NetworkAssignment(virtualDatacenter, rack, vlanNetwork);
                if (vlanNetwork.getTag() == null)
                {

                    List<Integer> vlansUsed = vlanNetworkDao.getVLANsIdUsedInRack(rack);
                    vlansUsed.addAll(getPublicVLANIdsFROMVLANNetworkList(vlanNetworkDao
                        .findPublicVLANNetworksByRack(rack)));
                    Integer freeTag = getFreeVLANFromUsedList(vlansUsed, rack);
                    log.debug("The VLAN tag chosen for the vlan network: {} is : {}",
                        vlanNetwork.getId(), freeTag);
                    vlanNetwork.setTag(freeTag);

                    vlanNetworkDao.flush();
                    // vlanNetworkDao.persist(vlanNetwork);
                }
                Rasd rasd = ipPoolManagement.getRasdRaw();
                rasd.setAllocationUnits(String.valueOf(vlanNetwork.getTag()));
                rasd.setParent(ipPoolManagement.getNetworkName());
                rasd.setConnection(physicalTarget.getVirtualSwitch());

                rasdDao.flush();
                // rasdDao.persist(rasd);

                netAssignDao.persist(nb);
            }// iterate over VlanNetwork
        }
        else
        {
            log.debug("The virtual machine has a network assigned, setting networking RASD to virtual machine");
            for (final IpPoolManagement ipPoolManagement : ippoolManagementList)
            {
                VLANNetwork vlanNetwork = ipPoolManagement.getVlanNetwork();
                Rasd rasd = ipPoolManagement.getRasdRaw();
                final Rack rack = physicalTarget.getRack();
                if (vlanNetwork.getTag() == null)
                {
                    List<Integer> vlansUsed = vlanNetworkDao.getVLANsIdUsedInRack(rack);
                    vlansUsed.addAll(getPublicVLANIdsFROMVLANNetworkList(vlanNetworkDao
                        .findPublicVLANNetworksByRack(rack)));
                    Integer freeTag = getFreeVLANFromUsedList(vlansUsed, rack);

                    log.debug("The VLAN tag chosen for the vlan network: {} is : {}",
                        vlanNetwork.getId(), freeTag);
                    vlanNetwork.setTag(freeTag);
                    final NetworkAssignment nb =
                        new NetworkAssignment(virtualDatacenter, rack, vlanNetwork);

                    vlanNetworkDao.flush();
                    netAssignDao.persist(nb);
                }
                rasd.setAllocationUnits(String.valueOf(vlanNetwork.getTag()));
                rasd.setParent(vlanNetwork.getName());
                rasd.setConnection(physicalTarget.getVirtualSwitch());

                rasdDao.flush();
            }// iterate over VlanNetwork

        }

    }

    /**
     * Rollback networking resources
     * 
     * @param session the session
     * @param virtualMachine the virtual machine
     * @param physicalTarget the physical machine
     */
    private void rollbackNetworkingResources(final Machine physicalTarget,
        final VirtualMachine virtualMachine)
    {

        List<IpPoolManagement> ippoolManagementList =
            ipPoolManDao.findByVirtualMachine(virtualMachine.getId());

        for (final IpPoolManagement ipPoolManagement : ippoolManagementList)
        {
            VLANNetwork vlanNetwork = ipPoolManagement.getVlanNetwork();

            final boolean assigned =
                ipPoolManDao.isVlanAssignedToDifferentVM(virtualMachine.getId(), vlanNetwork);

            if (!assigned)
            {
                if (vlanNetwork.getTag() != null)
                {
                    vlanNetwork.setTag(null);
                    vlanNetworkDao.flush();
                    // vlanNetworkDao.persist(vlanNetwork);
                }
                NetworkAssignment na = netAssignDao.findByVlanNetwork(vlanNetwork);
                if (na != null)
                {
                    netAssignDao.remove(na);
                }
            }
        }

    }

    /**
     * Set the resource usage on PhysicalMachine after instantiating the new VirtualMachine. It
     * access DB throw Hibernate.
     * 
     * @param machine, the machine to reduce/increase its resource capacity.
     * @param used, the VirtualImage requirements to substract/add.
     * @param isAdd, true if reducing the amount of resources on the PhysicalMachine. Else it adds
     *            capacity (as a rollback on VirtualImage deploy Exception).
     */
    protected void updateUsagePhysicalMachine(final Machine machine, final VirtualMachine used,
        final boolean isRollback)
    {

        final int newCpu =
            (isRollback ? machine.getVirtualCpusUsed() - used.getCpu() : machine
                .getVirtualCpusUsed() + used.getCpu());

        final int newRam =
            (isRollback ? machine.getVirtualRamUsedInMb() - used.getRam() : machine
                .getVirtualRamUsedInMb() + used.getRam());

        if (used.getVirtualImage().getStateful() == 1)
        {
            used.setHdInBytes(0l); // stateful virtual images doesn't use the datastores
        }

        final Long newHd =
            isRollback ? machine.getVirtualHardDiskUsedInBytes() - used.getHdInBytes() : machine
                .getVirtualHardDiskUsedInBytes() + used.getHdInBytes();

        // prevent to set negative usage
        machine.setVirtualCpusUsed(newCpu >= 0 ? newCpu : 0);
        machine.setVirtualRamUsedInMb(newRam >= 0 ? newRam : 0);
        machine.setVirtualHardDiskUsedInBytes(newHd >= 0 ? newHd : 0);

        datacenterRepo.updateMachine(machine);
    }

    /**
     * Updates the datastore with the used size by the virtual machine
     * 
     * @param virtual the virtual machine that contains the datastore to update
     * @param session the hibernate session
     */
    private void updateUsageDatastore(final VirtualMachine virtual, final boolean isRollback)
    {

        Datastore datastore = virtual.getDatastore();

        final Long actualSize = datastore.getUsedSize();
        final Long required =
            virtual.getVirtualImage().getStateful() == 1 ? 0 : virtual.getHdInBytes();
        // stateful virtual images doesn't use the datastores

        final Long newUsed = isRollback ? actualSize - required : actualSize + required;

        datastore.setUsedSize(newUsed >= 0 ? newUsed : 0); // prevent negative usage
        datastoreDao.flush();
    }

    /**
     * Gets a free VLAN from the list used VLAN
     * 
     * @param rack
     * @param vlan ports
     * @return
     * @throws SchedulerException
     */
    public Integer getFreeVLANFromUsedList(final List<Integer> vlanIds, Rack rack)
        throws NotEnoughResourcesException
    {
        Integer candidatePort = rack.getVlanIdMin();

        // Adding Vlans Id not to add

        vlanIds.addAll(getVlansIdAvoidAsCollection(rack));

        if (vlanIds.isEmpty())
        {
            return candidatePort;
        }

        // Create a HashSet which allows no duplicates
        HashSet<Integer> hashSet = new HashSet<Integer>(vlanIds);

        // Assign the HashSet to a new ArrayList
        List<Integer> vlanIdsOrdered = new ArrayList<Integer>(hashSet);
        Collections.sort(vlanIdsOrdered);

        List<Integer> vlanTemp = new ArrayList<Integer>(vlanIdsOrdered);

        // Removing id min to vlan id min
        for (Integer vlanId : vlanTemp)
        {
            if (vlanId.intValue() < rack.getVlanIdMin())
            {
                vlanIdsOrdered.remove(vlanId);
            }
        }

        // Checking the minimal interval
        if (vlanIdsOrdered.get(0).compareTo(rack.getVlanIdMin()) != 0)
        {
            return candidatePort;
        }

        int next = 1;

        // Searching a gap in the vlan used list
        for (int i = 0; i < vlanIdsOrdered.size(); i++)
        {
            if (vlanIds.get(i) == rack.getVlanIdMax())
            {
                throw new NotEnoughResourcesException("The maximun number of VLAN id has been reached");
            }
            if (next == vlanIdsOrdered.size()
                || vlanIdsOrdered.get(next) != vlanIdsOrdered.get(i) + 1)
            {
                return vlanIdsOrdered.get(i) + 1;
            }
            next++;
        }

        return candidatePort;
    }

    public Collection<Integer> getVlansIdAvoidAsCollection(Rack rack)
    {
        CsvReader reader = new CsvReader(new StringReader(rack.getVlansIdAvoided()));
        Collection<Integer> vlans_avoided_collection = new HashSet();
        String[] line = reader.readLine();

        if (line != null)
        {
            try
            {
                for (String vlan_id : line)
                {
                    if (vlan_id.split("-").length > 1)
                    {
                        String[] interval = vlan_id.split("-");
                        Integer min = Integer.valueOf(interval[0]);
                        Integer max = Integer.valueOf(interval[1]);
                        if (min.compareTo(max) > 0)
                        {
                            Integer temp = max;
                            max = min;
                            min = temp;
                        }
                        else
                        {
                            for (int i = min; i <= max; i++)
                            {
                                if (i > rack.getVlanIdMin() || i < rack.getVlanIdMax())
                                {
                                    vlans_avoided_collection.add(i);
                                }
                            }
                        }
                    }
                    else
                    {
                        Integer vlanIdCandidate = Integer.valueOf(vlan_id);
                        if (vlanIdCandidate > rack.getVlanIdMin()
                            || vlanIdCandidate < rack.getVlanIdMax())
                        {
                            vlans_avoided_collection.add(vlanIdCandidate);
                        }
                    }
                }
            }
            catch (NumberFormatException e)
            {
                log.debug("Ignoring not recognize vlan's id", e);
            }
        }
        return vlans_avoided_collection;
    }

    public List<Integer> getPublicVLANIdsFROMVLANNetworkList(List<VLANNetwork> vlanNetworkList)
    {
        List<Integer> publicIdsList = new ArrayList<Integer>();
        for (VLANNetwork vlanNetwork : vlanNetworkList)
        {
            publicIdsList.add(vlanNetwork.getTag());
        }

        return publicIdsList;
    }
}
