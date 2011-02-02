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

package com.abiquo.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.api.exceptions.EventingException;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.enumerator.HypervisorType;
import com.abiquo.vsm.client.VSMClient;

/**
 * Eventing support utility class for sending subscribe messages to be monitored by the abicloud
 * virtual system monitor. By default the notifications will be sent to the eventSink servlet
 * 
 * @author pnavarro
 */
public final class EventingSupport
{

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(EventingSupport.class);

    /**
     * Instantiates a new eventing support.
     */
    private EventingSupport()
    {
    }

    /**
     * It sends a subscription message to the abicloud virtualSystemMonitor.
     * 
     * @param virtualSystemAddress the virtualSystemAddress of the hypervisor
     * @param virtualSystemType the virtual system type
     * @param virtualSystemID the UUID of the virtual system
     * @param virtualSystemMonitorAddress the abicloud virtual system monitor address
     * @throws EventingException the eventing exception
     */
    public static void subscribe(final String virtualSystemAddress,
        final HypervisorType hypervisorType, final String virtualSystemID,
        final String virtualSystemMonitorAddress)
    {
        try
        {
            VSMClient vsmClient = new VSMClient(virtualSystemMonitorAddress);
            vsmClient.subscribe(virtualSystemAddress, hypervisorType.name(), virtualSystemID);
        }
        catch (Exception e)
        {
            throw new EventingException(e);
        }
    }

    /**
     * It sends a unsubscription message to the abicloud virtualSystemMonitor.
     * 
     * @param virtualSystemID the UUID of the virtual system
     * @param virtualSystemMonitorAddress the abicloud virtual system monitor address
     * @throws EventingException the eventing exception
     */
    public static void unsubscribe(final String virtualSystemID,
        final String virtualSystemMonitorAddress) throws EventingException
    {
        try
        {
            VSMClient vsmClient = new VSMClient(virtualSystemMonitorAddress);
            vsmClient.unsubscribe(virtualSystemID);
        }
        catch (Exception e)
        {
            throw new EventingException(e);
        }
    }

    /**
     * Helper to unsubscribe to a specific event of a virtual machine.
     * 
     * @param virtualMachine the virtual machine
     * @param virtualSystemMonitorAddress the virtual system monitor address
     * @throws EventingException
     */
    public static void unsubscribeEvent(final VirtualMachine virtualMachine,
        final String virtualSystemMonitorAddress) throws EventingException
    {
        unsubscribe(virtualMachine.getName(), virtualSystemMonitorAddress);

    }

    /**
     * Helper to subscribe to a virtual machine state.
     * 
     * @param virtualMachine the virtual machine to subscribe
     * @param virtualSystemMonitorAddress the virtual system monitor address
     * @throws EventingException
     */
    public static void subscribeEvent(final VirtualMachine virtualMachine,
        final String virtualSystemMonitorAddress) throws EventingException
    {
        Hypervisor hypervisor = (Hypervisor) virtualMachine.getHypervisor();

        if (hypervisor != null)
        {
            String virtualSystemAddress =
                "http://" + hypervisor.getIp() + ":" + hypervisor.getPort() + "/";
            subscribe(virtualSystemAddress, hypervisor.getType(), virtualMachine.getName(),
                virtualSystemMonitorAddress);
        }
        else
        {
            logger.error("Error while subscribing to the virtual machine with id: "
                + virtualMachine.getId() + ". The hypervisor is not setted.");
        }
    }

    /**
     * Subscribes to all events in the VirtualAppliance.
     * 
     * @param virtualAppliance the virtual appliance
     * @throws EventingException
     */
    public static void subscribeToAllVA(final VirtualAppliance virtualAppliance,
        final String virtualSystemMonitorAddress) throws EventingException
    {

        for (NodeVirtualImage node : virtualAppliance.getNodes())
        {
            VirtualMachine virtualMachine = node.getVirtualMachine();

            if (virtualMachine != null)
            {
                subscribeEvent(virtualMachine, virtualSystemMonitorAddress);
            }
            else
            {
                logger.error("Error. The virtual machine id is NULL for the node with id: "
                    + node.getId());
            }
        }

    }

    /**
     * Updates the subscription of the VirtualAppliance
     * 
     * @param virtualAppliance the virtual appliance
     * @throws EventingException
     */
    public static void updateSubscription(final VirtualAppliance virtualAppliance,
        final String virtualSystemMonitorAddress) throws EventingException
    {
        for (NodeVirtualImage node : virtualAppliance.getNodes())
        {
            VirtualMachine vm = node.getVirtualMachine();
            if (vm.getState() == State.NOT_DEPLOYED)
            {
                subscribeEvent(vm, virtualSystemMonitorAddress);
            }
        }

    }

    /**
     * Unsubscribes to all events in the VirtualAppliance.
     * 
     * @param virtualAppliance the virtual appliance
     * @throws EventingException
     */
    // public static void unsubscribeToAllVA(final VirtualAppliance virtualAppliance)
    // {
    // String virtualSystemMonitorAddress = null;
    //
    // try
    // {
    // virtualSystemMonitorAddress =
    // RemoteServiceUtils.getVirtualSystemMonitorFromVA(virtualAppliance);
    // }
    // catch (Exception e)
    // {
    // logger.trace("An error occured while finding the VirtualSystemMonitor", e
    // .getStackTrace()[0]);
    //
    // // Do not unsubscribe
    // return;
    // }
    //
    // for (Node< ? > node : virtualAppliance.getNodes())
    // {
    // if (node.isNodeTypeVirtualImage())
    // {
    // NodeVirtualImage nodeVI = (NodeVirtualImage) node;
    //
    // try
    // {
    // unsubscribeEvent(nodeVI.getVirtualMachine(), virtualSystemMonitorAddress);
    // }
    // catch (EventingException e)
    // {
    // // This exception is thrown because we are not following the WS-eventing
    // // standard in the unsubscription process
    // logger.trace("As in the unsubscription message we are not following the"
    // + " WS-eventing standard this exception can be thrown: {}", e
    // .getStackTrace()[0]);
    // }
    // }
    // }
    //
    // }

    /**
     * Subscribes to events pulling to all events in the VirtualAppliance to an specific event.
     * 
     * @param virtualAppliance the virtual appliance
     * @throws EventingException
     */
    // public static void subscribePullToAllVA(final VirtualAppliance virtualAppliance)
    // throws EventingException
    // {
    // String virtualSystemMonitorAddress;
    // try
    // {
    // virtualSystemMonitorAddress =
    // RemoteServiceUtils.getVirtualSystemMonitorFromVA(virtualAppliance);
    //
    // for (Node node : virtualAppliance.getNodes())
    // {
    // if (node.isNodeTypeVirtualImage())
    // {
    // // Convert to virtualImage node
    // NodeVirtualImage nodeVI = (NodeVirtualImage) node;
    // subscribePullEvent(nodeVI.getVirtualMachine(), virtualSystemMonitorAddress);
    // }
    // }
    //
    // }
    // catch (PersistenceException e)
    // {
    // logger.trace("Exists a problem finding the VirtualSystemMonitor", e.getStackTrace()[0]);
    // }
    // catch (RemoteServiceException e)
    // {
    // logger.trace("Exists a problem finding the VirtualSystemMonitor", e.getStackTrace()[0]);
    // }
    //
    // }

    public static void subscribePullEventToVM(final VirtualMachine virtualMachine,
        final String virtualSystemMonitorAddress) throws EventingException
    {
        subscribePullEvent(virtualMachine, virtualSystemMonitorAddress);
    }

    /**
     * Helper to subscribe to pulling events to a virtual machine state.
     * 
     * @param virtualMachine the virtual machine to subscribe
     * @param eventType the eventType to subscribe
     * @throws EventingException
     */
    public static void subscribePullEvent(final VirtualMachine virtualMachine,
        final String virtualSystemMonitorAddress) throws EventingException
    {
        Hypervisor hypervisor = (Hypervisor) virtualMachine.getHypervisor();

        if (hypervisor != null)
        {
            String virtualSystemAddress =
                "http://" + hypervisor.getIp() + ":" + hypervisor.getPort() + "/";
            subscribePull(virtualSystemAddress, virtualMachine.getName(),
                virtualSystemMonitorAddress);
        }

    }

    /**
     * It sends a subscription message to the abicloud virtualSystemMonitor with the PULL develiry
     * mode.
     * 
     * @param virtualSystemAddress the virtualSystemAddress of the hypervisor
     * @param virtualSystemID the UUID of the virtual system
     * @param virtualSystemMonitorAddress the abicloud virtual system monitor address
     * @param eventType the event type to monitor
     * @throws EventingException the eventing exception
     */
    public static void subscribePull(final String virtualSystemAddress,
        final String virtualSystemID, final String virtualSystemMonitorAddress)
        throws EventingException
    {
        try
        {
            VSMClient vsmClient = new VSMClient(virtualSystemMonitorAddress);
            vsmClient.publishState(virtualSystemAddress, virtualSystemID);
        }
        catch (Exception e)
        {
            throw new EventingException(e);
        }
    }

    /**
     * Starts the monitoring in the VSM of a physical machine
     * 
     * @param virtualSystemAddress the physical machine addres to monitor
     * @param virtualSystemType the hypervisor type
     * @param virtualSystemMonitorAddress the URI of the VSM service
     * @param user the admin hypervisor user
     * @param password the admin hypervisor password
     * @throws EventingException
     */
    public static void monitorPhysicalMachine(final String virtualSystemAddress,
        final HypervisorType hypervisorType, final String virtualSystemMonitorAddress,
        final String user, final String password) throws EventingException
    {
        try
        {
            VSMClient vsmClient = new VSMClient(virtualSystemMonitorAddress);
            vsmClient.monitor(virtualSystemAddress, hypervisorType.name(), user, password);
        }
        catch (Exception e)
        {
            throw new EventingException(e);
        }
    }

    /**
     * Stops the monitoring in the VSM of a physical machine
     * 
     * @param virtualSystemAddress TODO
     * @param virtualSystemType the hypervisor type
     * @param virtualSystemMonitorAddress the URI of the VSM service
     * @param user the admin hypervisor user
     * @param password the admin hypervisor password
     * @param virtualSystemAddress the physical machine addres to monitor
     * @throws EventingException
     */
    public static void unMonitorPhysicalMachine(String virtualSystemAddress,
        final String virtualSystemMonitorAddress, final String user, final String password)
        throws EventingException
    {
        try
        {
            VSMClient vsmClient = new VSMClient(virtualSystemMonitorAddress);
            vsmClient.shutdown(virtualSystemAddress);
        }
        catch (Exception e)
        {
            throw new EventingException(e);
        }
    }

}
