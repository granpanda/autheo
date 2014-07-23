package gp.e3.autheo.authorization.service;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authentication.service.resources.commons.HttpCommonResponses;
import gp.e3.autheo.authorization.domain.business.RoleBusiness;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/roles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource {
	
	private final RoleBusiness roleBusiness;
	
	public RoleResource(RoleBusiness roleBusiness) {
		
		this.roleBusiness = roleBusiness;
	}

	@POST
	public Response createRole(Role role) {
		
		Response response = null;
		
		if (Role.isValidRole(role)) {
			
			Role createdRole = roleBusiness.createRole(role);
			
			if (createdRole != null) {
				
				response = Response.status(201).entity(createdRole).build();
				
			} else {
				
				String errorMessage = "The role with name: " + role.getName() + " could not be created.";
				response = Response.status(500).entity(errorMessage).build();
			}
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
	
	@GET
	@Path("/{roleName}")
	public Response getRoleByName(@PathParam("roleName") String roleName) {
		
		Response response = null;
		
		if (StringValidator.isValidString(roleName)) {
			
			Role retrievedRole = roleBusiness.getRoleByName(roleName);
			response = Response.status(200).entity(retrievedRole).build();
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
	
	@GET
	public Response getAllRolesNames() {
		
		List<String> rolesNames = roleBusiness.getAllRolesNames();
		return Response.status(200).entity(rolesNames).build();
	}
	
	@PUT
	@Path("/{roleName}")
	public Response updateRole(@PathParam("roleName") String roleName, Role updatedRole) {
		
		Response response = null;
		
		if (StringValidator.isValidString(roleName) && Role.isValidRole(updatedRole)) {
			
			roleBusiness.updateRole(roleName, updatedRole.getPermissions());
			response = Response.status(200).build();
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
	
	@DELETE
	@Path("/{roleName}")
	public Response deleteRole(@PathParam("roleName") String roleName) {
		
		Response response = null;
		
		if (StringValidator.isValidString(roleName)) {
			
			roleBusiness.deleteRole(roleName);
			response = Response.status(200).build();
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
	
	@POST
	@Path("/{roleName}/users")
	public Response addUserToRole(@PathParam("roleName") String roleName, User user) {
		
		Response response = null;
		
		if (StringValidator.isValidString(roleName) && user != null) {
			
			String username = user.getUsername();
			if (StringValidator.isValidString(username)) {
				
				roleBusiness.addUserToRole(username, roleName);
				response = Response.status(201).build();
				
			} else {
				
				response = HttpCommonResponses.getInvalidSyntaxResponse();
			}
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
	
	@DELETE
	@Path("/{roleName}/users/{username}")
	public Response removeUserFromRole(@PathParam("roleName") String roleName, @PathParam("username") String username) {
		
		Response response = null;
		
		if (StringValidator.isValidString(roleName) && StringValidator.isValidString(username)) {
			
			roleBusiness.removeUserFromRole(username, roleName);
			response = Response.status(200).build();
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
	
	@GET
	@Path("/{roleName}/permissions")
	public Response getAllPermissionsOfAGivenRole(@PathParam("roleName") String roleName) {
		
		Response response = null;
		
		if (StringValidator.isValidString(roleName)) {
			
			List<Permission> rolePermissions = roleBusiness.getAllPermissionsOfAGivenRole(roleName);
			response = Response.status(200).entity(rolePermissions).build();
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
}