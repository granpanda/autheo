package gp.e3.autheo.authorization.service;

import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.authentication.service.resources.commons.HttpCommonResponses;
import gp.e3.autheo.authorization.domain.business.PermissionBusiness;
import gp.e3.autheo.authorization.domain.entities.Permission;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/permissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/permissions", description = "Role related operations")
public class PermissionResource {
	
	private final PermissionBusiness permissionBusiness;

	public PermissionResource(PermissionBusiness permissionBusiness) {
		
		this.permissionBusiness = permissionBusiness;
	}
	
	@POST
	@ApiOperation(value = "Create a new permission.", response = Permission.class)
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "The user is unauthorized."),
			@ApiResponse(code = 403, message = "The user is forbidden.")
	})
	public Response createPermission(Permission permission) {
		
		Response response = null;
		
		if (Permission.isValidPermission(permission)) {
			
			try {
				
				Permission createdPermission = permissionBusiness.createPermission(permission);
				response = Response.status(201).entity(createdPermission).build();
				
			} catch (DuplicateIdException e) {
				
				response = Response.status(500).entity(e.getMessage()).build();
			}
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
	
	@GET
	@Path("/{permissionId}")
	@ApiOperation(value = "Get permission by ID.", response = Permission.class)
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "The user is unauthorized."),
			@ApiResponse(code = 403, message = "The user is forbidden.")
	})
	public Response getPermissionById(@PathParam("permissionId") String permissionId) {
		
		Response response = null;
		
		try {
			
			int permissionIdInt = Integer.parseInt(permissionId);
			
			Permission retrievedPermission = permissionBusiness.getPermissionById(permissionIdInt);
			response = Response.status(200).entity(retrievedPermission).build();
			
		} catch (Exception e) {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
	
	@GET
	@ApiOperation(value = "Get all permissions.", response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "The user is unauthorized."),
			@ApiResponse(code = 403, message = "The user is forbidden.")
	})
	public Response getAllPermissions() {
		
		List<Permission> permissionsList = permissionBusiness.getAllPermissions();
		return Response.status(200).entity(permissionsList).build();
	}
	
	@DELETE
	@Path("/{permissionId}")
	@ApiOperation(value = "Delete a permission by ID")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "The user is unauthorized."),
			@ApiResponse(code = 403, message = "The user is forbidden.")
	})
	public Response deletePermission(@PathParam("permissionId") String permissionId) {
		
		Response response = null;
		
		try {
			
			int permissionIdInt = Integer.parseInt(permissionId);
			
			/*
			 * Disassociate the permission from all roles and delete it from the system.
			 */
			permissionBusiness.deletePermission(permissionIdInt);
			response = Response.status(200).build();
			
		} catch (Exception e) {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
}