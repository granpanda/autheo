package gp.e3.autheo.authentication.service.resources;

import gp.e3.autheo.authentication.domain.business.ApiUserBusiness;
import gp.e3.autheo.authentication.domain.entities.ApiUser;
import gp.e3.autheo.authentication.infrastructure.exceptions.CheckedIllegalArgumentException;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.authentication.service.resources.commons.HttpCommonResponses;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.Api;

@Path("/api-users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/api-users", description = "Api user related operations")
public class ApiUserResource {
	
	private final ApiUserBusiness apiUserBusiness;
	
	public ApiUserResource(ApiUserBusiness apiUserBusiness) {
		this.apiUserBusiness = apiUserBusiness;
	}

	@POST
	public Response createApiUser(ApiUser newApiUser) {
		
		Response response = null;
		
		if (ApiUser.isValidApiUser(newApiUser)) {
			
			try {
				
				ApiUser createdApiUser = apiUserBusiness.createApiUser(newApiUser);
				response = Response.status(201).entity(createdApiUser).build();
				
			} catch (DuplicateIdException | CheckedIllegalArgumentException e) {
				
				e.printStackTrace();
				response = Response.status(500).entity(e.getMessage()).build();
			}
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
	
	@PUT
	@Path("/{username}")
	public Response updateApiUser(@PathParam("username") String username, ApiUser updatedApiUser) {
		
		Response response = null;
		
		if (StringValidator.isValidString(username) && ApiUser.isValidApiUser(updatedApiUser)) {
			
			apiUserBusiness.updateApiUser(username, updatedApiUser);
			response = Response.status(200).build();
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
	
	@DELETE
	@Path("/{username}")
	public Response updateApiUser(@PathParam("username") String username) {
		
		Response response = null;
		
		if (StringValidator.isValidString(username)) {
			
			apiUserBusiness.deleteApiUser(username);
			response = Response.status(200).build();
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
}