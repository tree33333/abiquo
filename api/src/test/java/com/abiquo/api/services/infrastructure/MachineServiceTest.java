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

package com.abiquo.api.services.infrastructure;

import static com.abiquo.server.core.cloud.State.RUNNING;

import javax.persistence.EntityManager;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.api.common.Assert;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.MachineService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;

public class MachineServiceTest extends AbstractGeneratorTest
{
    @AfterMethod
    public void tearDown()
    {
        tearDown("ip_pool_management", "rasd_management", "virtualapp", "nodevirtualimage", "node",
            "virtualmachine", "virtualimage", "virtualdatacenter", "vlan_network",
            "network_configuration", "dhcp_service", "remote_service", "hypervisor",
            "physicalmachine", "rack", "datacenter", "network", "user", "role", "enterprise");
    }

    @Test
    public void testDeleteMachineWithVirtualMachinesDeployed()
    {
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();
        Datacenter datacenter = hypervisor.getMachine().getDatacenter();
        VirtualDatacenter vdc = vdcGenerator.createInstance(datacenter);
        RemoteService rm =
            remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_SYSTEM_MONITOR,
                datacenter);

        VirtualImage image = virtualImageGenerator.createInstance(vdc.getEnterprise());
        VirtualAppliance vapp = virtualApplianceGenerator.createInstance(vdc);
        VirtualMachine vm =
            vmGenerator.createInstance(image, vdc.getEnterprise(), hypervisor, "vm_test");
        vm.setState(RUNNING);

        NodeVirtualImage node = new NodeVirtualImage("node_test", vapp, image, vm);

        hypervisor.getMachine().setHypervisor(hypervisor);

        setup(vdc.getEnterprise(), datacenter, rm, hypervisor.getMachine().getRack(), hypervisor
            .getMachine(), hypervisor, vdc, image, vapp, vm.getUser().getRole(), vm.getUser(), vm,
            node);

        int machineId = hypervisor.getMachine().getId();

        EntityManager em = getEntityManager();
        EntityManagerHelper.beginReadWriteTransaction(em);

        MachineService service = new MachineService(em);
        service.removeMachine(machineId);

        EntityManagerHelper.commit(em);

        EntityManagerHelper.beginRollbackTransaction(em);
        service = new MachineService(em);

        try
        {
            Machine m = service.getMachine(machineId);
        }
        catch (NotFoundException e)
        {
            Assert.assertEquals(e.getMessage(), "The requested machine does not exist");
        }

        VirtualMachineService vmService = new VirtualMachineService(em);

        VirtualMachine virtualMachine = vmService.getVirtualMachine(vm.getId());
        Assert.assertNull(virtualMachine.getHypervisor());
        Assert.assertNull(virtualMachine.getDatastore());
        Assert.assertEquals(virtualMachine.getState(), State.NOT_DEPLOYED);
    }
}
