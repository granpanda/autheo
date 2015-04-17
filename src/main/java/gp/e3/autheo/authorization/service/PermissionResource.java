package gp.e3.autheo.authorization.service;

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
import javax.ws.rs.core.Response.Status;

@Path("/permissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PermissionResource {

	private final PermissionBusiness permissionBusiness;

	public PermissionResource(PermissionBusiness permissionBusiness) {

		this.permissionBusiness = permissionBusiness;
	}

	@POST
	public Response createPermission(Permission permission) {

		Response response = null;

		if (Permission.isValidPermission(permission)) {

			long permissionId = permissionBusiness.createPermission(permission);

			if (permissionId != 0) {

				response = Response.status(201).entity(permissionId).build();

			} else {

				String errorMessage = "The permission could not be created.";
				response = Response.status(500).entity(errorMessage).build();
			}

		} else {

			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}

		return response;
	}

	@GET
	@Path("/{permissionId}")
	public Response getPermissionById(@PathParam("permissionId") int permissionId) {

		Permission retrievedPermission = permissionBusiness.getPermissionById(permissionId);
		Response response = Response.status(200).entity(retrievedPermission).build();

		return response;
	}

	@GET
	public Response getAllPermissions() {

		List<Permission> permissionsList = permissionBusiness.getAllPermissions();
		return Response.status(200).entity(permissionsList).build();
	}

	@DELETE
	@Path("/{permissionId}")
	public Response deletePermission(@PathParam("permissionId") int permissionId) {

		Response response = null;
		// Disassociate the permission from all roles and delete it from the system.
		boolean permissionWasDeleted = permissionBusiness.deletePermission(permissionId);
		
		if (permissionWasDeleted) {
			
			response = Response.status(200).build();
			
		} else {
			
			response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		return response;
	}
}