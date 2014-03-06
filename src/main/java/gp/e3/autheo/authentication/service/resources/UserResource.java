package gp.e3.autheo.authentication.service.resources;

import gp.e3.autheo.authentication.domain.business.UserBusiness;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;

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

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	
	private final UserBusiness userBusiness;

	public UserResource(UserBusiness userBusiness) {
		
		this.userBusiness = userBusiness;
	}
	
	@POST
	public Response createUser(User newUser) {
		
		Response response = null;
		
		try {
			
			userBusiness.createUser(newUser);
			response = Response.status(201).entity(newUser).build();
			
		} catch (DuplicateIdException e) {
			
			response = Response.status(500).entity(e.getMessage()).build();
		}
		
		return response;
	}
	
	@GET
	@Path("/{ username }")
	public Response getUserByUsername(@PathParam("username") String username) {
		
		User user = userBusiness.getUserByUsername(username);
		return Response.status(200).entity(user).build();
	}
	
	@GET
	public Response getAllUsers() {
		
		List<User> users = userBusiness.getAllUsers();
		return Response.status(200).entity(users).build();
	}
	
	@PUT
	@Path("/{ username }")
	public Response updateUser(@PathParam("username") String username, User updatedUser) {
		
		userBusiness.updateUser(username, updatedUser);
		return Response.status(200).build();
	}
	
	@DELETE
	@Path("/{ username }")
	public Response deleteUser(@PathParam("username") String username) {
		
		userBusiness.deleteUser(username);
		return Response.status(200).build();
	}
}