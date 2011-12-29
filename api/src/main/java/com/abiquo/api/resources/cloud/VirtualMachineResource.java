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

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.aimstub.Datastore;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.TaskResourceUtils;
import com.abiquo.api.services.TaskService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineTaskDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineInstanceDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;
import com.abiquo.server.core.task.enums.TaskOwnerType;

@Parent(VirtualMachinesResource.class)
@Controller
@Path(VirtualMachineResource.VIRTUAL_MACHINE_PARAM)
public class VirtualMachineResource extends AbstractResource
{

    private static final Logger logger = LoggerFactory.getLogger(VirtualMachineResource.class);

    private final static Integer TIMEOUT = Integer.parseInt(System.getProperty(
        "abiquo.nodecollector.timeout", "0")) * 2; // 3 minutes

    public static final String VIRTUAL_MACHINE = "virtualmachine";

    public static final String VIRTUAL_MACHINE_PARAM = "{" + VIRTUAL_MACHINE + "}";

    public static final String VIRTUAL_MACHINE_ACTION_DEPLOY = "/action/deploy";

    public static final String VIRTUAL_MACHINE_ACTION_UNDEPLOY = "/action/undeploy";

    public static final String VIRTUAL_MACHINE_ACTION_RESET = "/action/reset";

    public static final String VIRTUAL_MACHINE_STATE = "/state";

    // Chef constants to help link builders. Method implementation are premium.
    public static final String VIRTUAL_MACHINE_RUNLIST_PATH = "/config/runlist";

    public static final String VIRTUAL_MACHINE_BOOTSTRAP_PATH = "/config/bootstrap";

    public static final String VM_NODE_MEDIA_TYPE = "application/vnd.vm-node+xml";

    public static final String VIRTUAL_MACHINE_ACTION_DEPLOY_REL = "deploy";

    public static final String VIRTUAL_MACHINE_ACTION_SNAPSHOT_REL = "instance";

    public static final String VIRTUAL_MACHINE_ACTION_SNAPSHOT = "/action/instance";

    public static final String VIRTUAL_MACHINE_ACTION_UNDEPLOY_REL = "undeploy";

    public static final String VIRTUAL_MACHINE_STATE_REL = "state";
    
    public static final String FORCE_UNDEPLOY = "force";

    @Autowired
    private VirtualMachineService vmService;

    @Autowired
    private TaskService taskService;

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
    @Produces(MediaType.APPLICATION_XML)
    public VirtualMachineDto getVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);

        return createTransferObject(vm, vdcId, vappId, restBuilder, getVolumeIds(vm),
            getDiskIds(vm), vm.getIps());
    }

    /***
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return a link where you can keep track of the progress and the virtual machine.
     * @throws Exception AcceptedRequestDto
     */
    @PUT
    public AcceptedRequestDto<String> updateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        final VirtualMachineDto dto, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        String taskId = vmService.reconfigureVirtualMachine(vdcId, vappId, vmId, dto);

        if (taskId == null)
        {
            // If the link is null no Task was performed
            return null;
        }

        return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
    }

    /**
     * Updates this virtual Machine Node information (e.g. name)
     * 
     * @param vdcId
     * @param vappId
     * @param vmId
     * @param dto
     * @param restBuilder
     * @param uriInfo
     * @return
     * @throws Exception
     */
    @PUT
    @Consumes(VM_NODE_MEDIA_TYPE)
    public VirtualMachineDto updateVirtualMachineNode(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        final VirtualMachineDto dto, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        // TODO: validatePathParameters(vdcId, vappId, vmId);

        VirtualMachine modifiedVMachine = vmService.modifyVirtualMachine(vdcId, vappId, vmId);

        final VirtualMachineDto modifiedVMachineDto =
            VirtualMachineResource.createTransferObject(modifiedVMachine, vdcId, vappId,
                restBuilder, null, null, null);

        return modifiedVMachineDto;
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
     * @param restBuilder injected restbuilder context parameter * @return a link where you can keep
     *            track of the progress and a message.
     * @throws Exception
     */
    @PUT
    @Path(VIRTUAL_MACHINE_STATE)
    public AcceptedRequestDto<String> powerStateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineStateDto state, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        VirtualMachineState newState = validateState(state);
        String taskId = vmService.applyVirtualMachineState(vmId, vappId, vdcId, newState);

        // If the link is null no Task was performed
        if (taskId == null)
        {
            throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
        }

        return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
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
    @Path(VIRTUAL_MACHINE_STATE)
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
        stateDto.setPower(vm.getState());
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
        if (!VirtualMachineState.ON.equals(state.getPower())
            && !VirtualMachineState.OFF.equals(state.getPower())
            && !VirtualMachineState.PAUSED.equals(state.getPower()))
        {
            throw new BadRequestException(APIError.VIRTUAL_MACHINE_EDIT_STATE);
        }
        return state.getPower();
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
     * @param forceSoftLimits dto of options * @return a link where you can keep track of the
     *            progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_ACTION_DEPLOY)
    @Consumes(MediaType.APPLICATION_XML)
    public AcceptedRequestDto<String> deployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineTaskDto forceSoftLimits, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        String taskId =
            vmService.deployVirtualMachine(vmId, vappId, vdcId,
                forceSoftLimits.isForceEnterpriseSoftLimits());

        // If the link is null no Task was performed
        if (taskId == null)
        {
            throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
        }

        return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
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
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_ACTION_DEPLOY)
    public AcceptedRequestDto<String> deployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder, @Context final UriInfo uriInfo) throws Exception
    {

        String taskId = vmService.deployVirtualMachine(vmId, vappId, vdcId, false);

        // If the link is null no Task was performed
        if (taskId == null)
        {
            throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
        }

        return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
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
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_ACTION_UNDEPLOY)
    public AcceptedRequestDto<String> undeployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineTaskDto taskOptions,
        @Context final IRESTBuilder restBuilder, @Context final UriInfo uriInfo) throws Exception
    {
        Boolean forceUndeploy;
        if (taskOptions.getForceUndeploy() == null)
        {
            forceUndeploy = Boolean.FALSE;
        }
        else
        {
            forceUndeploy = taskOptions.getForceUndeploy();
        }
        String taskId = vmService.undeployVirtualMachine(vmId, vappId, vdcId, forceUndeploy);

        // If the link is null no Task was performed
        if (taskId == null)
        {
            throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
        }

        return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
    }

    /**
     * Snapshot a {@link VirtualMachine}.>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_ACTION_SNAPSHOT)
    @Consumes(MediaType.APPLICATION_XML)
    public AcceptedRequestDto<String> snapshotVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineInstanceDto snapshotData, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        String taskId =
            vmService.snapshotVirtualMachine(vmId, vappId, vdcId, snapshotData.getSnapshotName());

        if (taskId == null)
        {
            // If the link is null no Task was performed
            throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
        }

        return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
    }

    /**
     * Converts to the transfer object for the VirtualMachine POJO when the request is from the
     * /cloud URI
     * 
     * @param v virtual machine
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param restBuilder {@link IRESTBuilder} object injected by context.
     * @return the generate {@link VirtualMachineDto} object.
     * @throws Exception
     */
    public static VirtualMachineWithNodeDto createNodeTransferObject(final NodeVirtualImage v,
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder,
        final Integer[] volumeIds, final Integer[] diskIds, final List<IpPoolManagement> ips)
        throws Exception
    {
        VirtualMachineWithNodeDto dto = new VirtualMachineWithNodeDto();
        dto.setUuid(v.getVirtualMachine().getUuid());
        dto.setCpu(v.getVirtualMachine().getCpu());
        dto.setDescription(v.getVirtualMachine().getDescription());
        dto.setHdInBytes(v.getVirtualMachine().getHdInBytes());
        dto.setHighDisponibility(v.getVirtualMachine().getHighDisponibility());
        dto.setId(v.getVirtualMachine().getId());
        // dto.setIdState(v.getidState)
        dto.setIdType(v.getVirtualMachine().getIdType());

        dto.setName(v.getVirtualMachine().getName());
        dto.setPassword(v.getVirtualMachine().getPassword());
        dto.setRam(v.getVirtualMachine().getRam());
        dto.setState(v.getVirtualMachine().getState());
        dto.setVdrpIP(v.getVirtualMachine().getVdrpIP());
        dto.setVdrpPort(v.getVirtualMachine().getVdrpPort());
        dto.setNodeId(v.getId());
        dto.setNodeName(v.getName());
        dto.setX(v.getX());
        dto.setY(v.getY());
        final Hypervisor hypervisor = v.getVirtualMachine().getHypervisor();
        final Machine machine = hypervisor == null ? null : hypervisor.getMachine();
        final Rack rack = machine == null ? null : machine.getRack();

        final Enterprise enterprise =
            v.getVirtualMachine().getEnterprise() == null ? null : v.getVirtualMachine()
                .getEnterprise();
        final User user =
            v.getVirtualMachine().getUser() == null ? null : v.getVirtualMachine().getUser();
        final VirtualMachineTemplate virtualImage =
            v.getVirtualImage() == null ? null : v.getVirtualImage();

        if (!v.getVirtualMachine().isImported())
        {
            dto.addLink(restBuilder.buildVirtualMachineTemplateLink(virtualImage.getEnterprise()
                .getId(), virtualImage.getRepository().getDatacenter().getId(), virtualImage.getId()));
        }
        else
        {
            // imported virtual machines
            dto.addLink(restBuilder.buildVirtualMachineTemplateLink(virtualImage.getEnterprise()
                .getId(), v.getVirtualMachine().getHypervisor().getMachine().getRack().getDatacenter().getId(), v.getVirtualImage().getId()));
        }
        
        dto.addLinks(restBuilder.buildVirtualMachineCloudAdminLinks(vdcId, vappId, v
            .getVirtualMachine().getId(), rack == null ? null : rack.getDatacenter().getId(),
            rack == null ? null : rack.getId(), machine == null ? null : machine.getId(),
            enterprise == null ? null : enterprise.getId(), user == null ? null : user.getId(), v
                .getVirtualMachine().isChefEnabled(), volumeIds, diskIds, ips));

        TaskResourceUtils.addTasksLink(dto, dto.getEditLink());

        return dto;
    }

    @Deprecated
    // use the integer based version
    public static VirtualMachineDto createTransferObject(final VirtualMachine v,
        final IRESTBuilder restBuilder)
    {
        VirtualMachineDto dto = new VirtualMachineDto();

        dto.setCpu(v.getCpu());
        dto.setDescription(v.getDescription());
        dto.setHdInBytes(v.getHdInBytes());
        dto.setHighDisponibility(v.getHighDisponibility());
        dto.setId(v.getId());
        // dto.setIdState(v.getidState)
        dto.setIdType(v.getIdType());

        dto.setName(v.getName());
        dto.setPassword(v.getPassword());
        dto.setRam(v.getRam());
        dto.setState(v.getState());
        dto.setVdrpIP(v.getVdrpIP());
        dto.setVdrpPort(v.getVdrpPort());

        final Hypervisor hypervisor = v.getHypervisor();
        final Machine machine = hypervisor == null ? null : hypervisor.getMachine();
        final Rack rack = machine == null ? null : machine.getRack();

        final Enterprise enterprise = v.getEnterprise() == null ? null : v.getEnterprise();
        final User user = v.getUser() == null ? null : v.getUser();

        dto.addLinks(restBuilder.buildVirtualMachineAdminLinks(rack == null ? null : rack
            .getDatacenter().getId(), rack == null ? null : rack.getId(), machine == null ? null
            : machine.getId(), enterprise == null ? null : enterprise.getId(), user == null ? null
            : user.getId()));

        final VirtualMachineTemplate vmtemplate = v.getVirtualMachineTemplate();
        if (vmtemplate.getRepository() != null)
        {
            dto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise()
                .getId(), vmtemplate.getRepository().getDatacenter().getId(), vmtemplate.getId()));
        }
        else
        {
            // imported virtual machines
            dto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise()
                .getId(), v.getHypervisor().getMachine().getRack().getDatacenter().getId(), vmtemplate.getId()));
        }
        
        TaskResourceUtils.addTasksLink(dto, dto.getEditLink());

        return dto;
    }

    public static VirtualMachineDto createTransferObject(final VirtualMachine v,
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder,
        final Integer[] volumeIds, final Integer diskIds[], final List<IpPoolManagement> ips)
    {

        VirtualMachineDto dto = new VirtualMachineDto();

        dto.setUuid(v.getUuid());
        dto.setCpu(v.getCpu());
        dto.setDescription(v.getDescription());
        dto.setHdInBytes(v.getHdInBytes());
        dto.setHighDisponibility(v.getHighDisponibility());
        dto.setId(v.getId());
        // dto.setIdState(v.getidState)
        if (v.getIdType() == 0)
        {
            dto.setIdType(com.abiquo.server.core.cloud.VirtualMachine.NOT_MANAGED);
        }
        else
        {
            dto.setIdType(com.abiquo.server.core.cloud.VirtualMachine.MANAGED);
        }

        dto.setName(v.getName());
        dto.setPassword(v.getPassword());
        dto.setRam(v.getRam());
        dto.setState(v.getState());
        dto.setVdrpIP(v.getVdrpIP());
        dto.setVdrpPort(v.getVdrpPort());

        final Hypervisor hypervisor = v.getHypervisor();
        final Machine machine = hypervisor == null ? null : hypervisor.getMachine();
        final Rack rack = machine == null ? null : machine.getRack();

        final Enterprise enterprise = v.getEnterprise() == null ? null : v.getEnterprise();
        final User user = v.getUser() == null ? null : v.getUser();

        dto.addLinks(restBuilder.buildVirtualMachineCloudAdminLinks(vdcId, vappId, v.getId(),
            rack == null ? null : rack.getDatacenter().getId(), rack == null ? null : rack.getId(),
            machine == null ? null : machine.getId(),
            enterprise == null ? null : enterprise.getId(), user == null ? null : user.getId(),
            v.isChefEnabled(), volumeIds, diskIds, ips));

        final VirtualMachineTemplate vmtemplate = v.getVirtualMachineTemplate();
        if (vmtemplate.getRepository() != null)
        {
            dto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise()
                .getId(), vmtemplate.getRepository().getDatacenter().getId(), vmtemplate.getId()));
        }
        else
        {
            // imported virtual machines
            dto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise()
                .getId(), v.getHypervisor().getMachine().getRack().getDatacenter().getId(), vmtemplate.getId()));
        }

        return dto;
    }

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
    @Produces(VM_NODE_MEDIA_TYPE)
    public VirtualMachineWithNodeDto getVirtualMachineWithNode(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        NodeVirtualImage node = vmService.getNodeVirtualImage(vdcId, vappId, vmId);

        return createNodeTransferObject(node, vdcId, vappId, restBuilder,
            getVolumeIds(node.getVirtualMachine()), getDiskIds(node.getVirtualMachine()), node
                .getVirtualMachine().getIps());
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path(TaskResourceUtils.TASKS_PATH)
    public TasksDto getTasks(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final UriInfo uriInfo) throws Exception
    {
        vmService.getVirtualMachine(vdcId, vappId, vmId);
        List<Task> tasks = taskService.findTasks(TaskOwnerType.VIRTUAL_MACHINE, vmId.toString());

        return TaskResourceUtils.transform(tasks, uriInfo);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path(TaskResourceUtils.TASK_PATH)
    public TaskDto getTask(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(TaskResourceUtils.TASK) @NotNull final String taskId,
        @Context final UriInfo uriInfo) throws Exception
    {
        vmService.getVirtualMachine(vdcId, vappId, vmId);
        Task task = taskService.findTask(vmId.toString(), taskId);

        return TaskResourceUtils.transform(task, uriInfo);
    }

    protected Integer[] getVolumeIds(final VirtualMachine vm)
    {
        return null; // Community impl
    }

    protected Integer[] getDiskIds(final VirtualMachine vm)
    {
        return null; // Community impl
    }

    /**
     * Reset a {@link VirtualMachine}.
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_ACTION_RESET)
    public AcceptedRequestDto<String> resetVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder, @Context final UriInfo uriInfo) throws Exception
    {
        String taskId =
            vmService.resetVirtualMachine(vmId, vappId, vdcId, VirtualMachineStateTransition.RESET);

        // If the link is null no Task was performed
        if (taskId == null)
        {
            throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
        }

        return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
    }

    protected AcceptedRequestDto<String> buildAcceptedRequestDtoWithTaskLink(final String taskId,
        final UriInfo uriInfo)
    {
        // Build task link
        String link = uriInfo.getRequestUri().toString();

        link = link.replaceAll("action.*", "");
        link = link.replaceAll("(/)*$", "");
        link = link.concat(TaskResourceUtils.TASKS_PATH).concat("/").concat(taskId);

        // Build AcceptedRequestDto
        AcceptedRequestDto<String> a202 = new AcceptedRequestDto<String>();
        a202.setStatusUrlLink(link);
        a202.setEntity("You can keep track of the progress in the link");

        return a202;
    }
}
