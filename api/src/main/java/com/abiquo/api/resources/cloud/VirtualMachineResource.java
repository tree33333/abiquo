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

package com.abiquo.api.resources.cloud;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDeployDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;

@Parent(VirtualMachinesResource.class)
@Controller
@Path(VirtualMachineResource.VIRTUAL_MACHINE_PARAM)
public class VirtualMachineResource extends AbstractResource
{
    public static final String VIRTUAL_MACHINE = "virtualmachine";

    public static final String VIRTUAL_MACHINE_PARAM = "{" + VIRTUAL_MACHINE + "}";

    public static final String VIRTUAL_MACHINE_ACTION_POWER_ON = "/action/poweron";

    public static final String VIRTUAL_MACHINE_ACTION_POWER_OFF = "/action/poweroff";

    public static final String VIRTUAL_MACHINE_ACTION_DEPLOY = "/action/deploy";

    public static final String VIRTUAL_MACHINE_ACTION_UNDEPLOY = "/action/undeploy";

    public static final String VIRTUAL_MACHINE_ACTION_RESUME = "/action/resume";

    public static final String VIRTUAL_MACHINE_ACTION_PAUSE = "/action/pause";

    public static final String VIRTUAL_MACHINE_STATE = "/state";

    // Chef constants to help link builders. Method implementation are premium.
    public static final String VIRTUAL_MACHINE_RUNLIST_PATH = "/config/runlist";

    public static final String VIRTUAL_MACHINE_BOOTSTRAP_PATH = "/config/bootstrap";

    @Autowired
    VirtualMachineService vmService;

    @Autowired
    VirtualMachineAllocatorService service;

    @Autowired
    UserService userService;

    @Autowired
    NetworkService networkService;

    /**
     * Return the virtual appliance if exists.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param restBuilder to build the links
     * @return the {@link VirtualApplianceDto} transfer object for the virtual appliance.
     * @throws Exception
     */
    @GET
    public VirtualMachineDto getVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);

        return VirtualMachinesResource.createCloudTransferObject(vm, vdcId, vappId, restBuilder);
    }

    @PUT
    public VirtualMachineDto updateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        final VirtualMachineDto dto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.updateVirtualMachine(vdcId, vappId, vmId, dto);

        return VirtualMachinesResource.createCloudTransferObject(vm, vdcId, vappId, restBuilder);
    }

    @PUT
    @Path("action/allocate")
    public synchronized VirtualMachineDto allocate(
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer virtualMachineId,
        final String forceEnterpriseLimitsStr, @Context final IRESTBuilder restBuilder)
        throws Exception
    {

        Boolean forceEnterpriseLimits = Boolean.parseBoolean(forceEnterpriseLimitsStr);
        // get user form the authentication layer
        // User user = userService.getCurrentUser();

        VirtualMachine vmachine =
            service.allocateVirtualMachine(virtualMachineId, virtualApplianceId,
                forceEnterpriseLimits);

        service.updateVirtualMachineUse(virtualApplianceId, vmachine);

        return createTransferObject(vmachine, restBuilder);
    }

    // TODO forceEnterpriseLimits = true

    @PUT
    @Path("action/checkedit")
    public synchronized void checkEditAllocate(
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer virtualMachineId,
        final VirtualMachineDto vmachine, @Context final IRESTBuilder restBuilder) throws Exception
    {
        // Boolean forceEnterpriseLimits = Boolean.parseBoolean(forceEnterpriseLimitsStr);
        // get user form the authentication layer
        // User user = userService.getCurrentUser();

        service.checkAllocate(virtualApplianceId, virtualMachineId, vmachine, true);
    }

    @DELETE
    @Path("action/deallocate")
    public synchronized void deallocate(
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer virtualMachineId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        service.deallocateVirtualMachine(virtualMachineId);
    }

    /**
     * Power on the VirtualMachine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     * @deprecated use
     *             {@link #powerStateVirtualMachine(Integer, Integer, Integer, VirtualMachineStateDto, IRESTBuilder)}
     *             instead
     */
    @POST
    @Deprecated
    @Path("action/poweron")
    public void powerOnVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());
        if (!vmService.sameState(vm, VirtualMachineState.ON))
        {

            vmService.changeVirtualMachineState(vmId, vappId, vdcId, VirtualMachineState.ON);

        }
    }

    /**
     * Power off the VirtualMachine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     * @deprecated use
     *             {@link #powerStateVirtualMachine(Integer, Integer, Integer, VirtualMachineStateDto, IRESTBuilder)}
     *             instead
     */
    @POST
    @Deprecated
    @Path("action/poweroff")
    public void powerOffVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());
        if (!vmService.sameState(vm, VirtualMachineState.OFF))
        {

            vmService.changeVirtualMachineState(vmId, vappId, vdcId, VirtualMachineState.OFF);

        }
    }

    /**
     * Change the {@link VirtualMachineState} the virtual machine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param state allowed
     *            <ul>
     *            <li><b>OFF</b></li>
     *            <li><b>ON</b></li>
     *            <li><b>PAUSED</b></li>
     *            </ul>
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @PUT
    @Path("state")
    public void powerStateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineStateDto state, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        VirtualMachineState newState = validateState(state);
        vmService.applyVirtualMachineState(vmId, vappId, vdcId, newState);
    }

    /**
     * Retrieve the {@link VirtualMachineState} the virtual machine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return state
     * @throws Exception
     */
    @GET
    @Path("state")
    public VirtualMachineStateDto stateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);

        VirtualMachineStateDto stateDto =
            virtualMachineStateToDto(vdcId, vappId, vmId, restBuilder, vm);
        return stateDto;
    }

    private VirtualMachineStateDto virtualMachineStateToDto(final Integer vdcId,
        final Integer vappId, final Integer vmId, final IRESTBuilder restBuilder,
        final VirtualMachine vm)
    {
        VirtualMachineStateDto stateDto = new VirtualMachineStateDto();
        stateDto.setPower(vm.getState().name());
        stateDto.addLinks(restBuilder.buildVirtualMachineStateLinks(vappId, vdcId, vmId));
        return stateDto;
    }

    /**
     * Validate that the state is allowed. <br>
     * 
     * @param state<ul>
     *            <li><b>OFF</b></li>
     *            <li><b>ON</b></li>
     *            <li><b>PAUSED</b></li>
     *            </ul>
     * @return State
     */
    private VirtualMachineState validateState(final VirtualMachineStateDto state)
    {
        if (!VirtualMachineState.ON.name().equals(state.getPower())
            && !VirtualMachineState.OFF.name().equals(state.getPower())
            && !VirtualMachineState.PAUSED.name().equals(state.getPower()))
        {
            throw new BadRequestException(APIError.VIRTUAL_MACHINE_EDIT_STATE);
        }
        return VirtualMachineState.valueOf(state.getPower());
    }

    /**
     * Resume the Virtual Machine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     * @deprecated use
     *             {@link #powerStateVirtualMachine(Integer, Integer, Integer, VirtualMachineStateDto, IRESTBuilder)}
     *             instead
     */
    @POST
    @Deprecated
    @Path("action/resume")
    public void resumeVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());

        if (!vmService.sameState(vm, VirtualMachineState.PAUSED))
        {
            vmService.changeVirtualMachineState(vmId, vappId, vdcId, VirtualMachineState.PAUSED);
        }
    }

    /**
     * Pause the VirtualMachine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception * @deprecated use
     *             {@link #powerStateVirtualMachine(Integer, Integer, Integer, VirtualMachineStateDto, IRESTBuilder)}
     *             instead
     */
    @Deprecated
    @POST
    @Path("action/pause")
    public void pauseVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());

        if (!vmService.sameState(vm, VirtualMachineState.PAUSED))
        {
            vmService.changeVirtualMachineState(vmId, vappId, vdcId, VirtualMachineState.PAUSED);
        }
    }

    /**
     * Deletes the virtual machine.<br>
     * A {@link VirtualMachine} can only be deleted if is in one allowed are NOT_ALLOCATED and
     * UNKNOWN. allowed
     * <ul>
     * <li><b>NOT_ALLOCATED</b></li>
     * <li><b>UNKNOWN</b></li>
     * </ul>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @DELETE
    public void deleteVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        vmService.deleteVirtualMachine(vmId, vappId, vdcId);
    }

    /**
     * Deploys a {@link VirtualMachine}. This involves some steps. <br>
     * <ul>
     * <li>Select a machine to allocate the virtual machine</li>
     * <li>Check limits</li>
     * <li>Check resources</li>
     * <li>Check remote services</li>
     * <li>In premium call initiator</li>
     * <li>Subscribe to VSM</li>
     * <li>Build the Task DTO</li>
     * <li>Enqueue in tarantino</li>
     * <li>Register in redis</li>
     * <li>Add Task DTO to rabbitmq</li>
     * <li>Enable the resource <code>Progress<code></li>
     * </ul>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @param forceSoftLimits dto of options
     * @throws Exception
     */
    @POST
    @Path("action/deploy")
    public AcceptedRequestDto<String> deployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineDeployDto forceSoftLimits, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        vmService.deployVirtualMachine(vmId, vappId, vdcId,
            forceSoftLimits.isForeceEnterpriseSoftLimits());

        AcceptedRequestDto<String> a202 = new AcceptedRequestDto<String>();
        a202.setStatusUrlLink("http://status");
        a202.setEntity("");

        return a202;
    }

    /**
     * Deploys a {@link VirtualMachine}. This involves some steps. <br>
     * <ul>
     * <li>Select a machine to allocate the virtual machine</li>
     * <li>Check limits</li>
     * <li>Check resources</li>
     * <li>Check remote services</li>
     * <li>In premium call initiator</li>
     * <li>Subscribe to VSM</li>
     * <li>Build the Task DTO</li>
     * <li>Enqueue in tarantino</li>
     * <li>Register in redis</li>
     * <li>Add Task DTO to rabbitmq</li>
     * <li>Enable the resource <code>Progress<code></li>
     * </ul>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @POST
    @Path("action/deploy")
    public void deployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        vmService.deployVirtualMachine(vmId, vappId, vdcId, false);
    }

    /**
     * Undeploys a {@link VirtualMachine}. This involves some steps. <br>
     * <ul>
     * <li>Deallocate the virtual machine</li>
     * <li>Delete the relations to the {@link Hypervisor}, {@link Datastore}</li>
     * <li>Set to NOT_DEPLOYED</li>
     * <li>Unsuscribe to VSM</li>
     * <li>Enqueue in tarantino</li>
     * <li>Register in redis</li>
     * <li>Add Task DTO to rabbitmq</li>
     * <li>Enable the resource <code>Progress<code></li>
     * </ul>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @POST
    @Path("action/undeploy")
    public void undeployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        vmService.undeployVirtualMachine(vmId, vappId, vdcId, false);
    }
    
    public static VirtualMachineDto createCloudTransferObject(final VirtualMachine v,
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachineDto vmDto = createTransferObject(v, vdcId, vappId, restBuilder);
        return vmDto;
    }

    public static VirtualMachineDto createTransferObject(final VirtualMachine v,
        final IRESTBuilder restBuilder)
    {
        VirtualMachineDto dto = new VirtualMachineDto();

        dto.setCpu(v.getCpu());
        dto.setDescription(v.getDescription());
        dto.setHd(v.getHdInBytes());
        dto.setHighDisponibility(v.getHighDisponibility());
        dto.setId(v.getId());
        // dto.setIdState(v.getidState)
        if (v.getIdType() == 0)
        {
        	dto.setType("NOT_MANAGED");
        }
        else
        {
        	dto.setType("MANAGED");
        }

        dto.setName(v.getName());
        dto.setPassword(v.getPassword());
        dto.setRam(v.getRam());
        dto.setState(v.getState());
        dto.setVdrpIP(v.getVdrpIP());
        dto.setVdrpPort(v.getVdrpPort());

        return dto;
    }

    public static VirtualMachineDto createTransferObject(final VirtualMachine v,
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder)
    {
        return createTransferObject(v, restBuilder);

    }
}
