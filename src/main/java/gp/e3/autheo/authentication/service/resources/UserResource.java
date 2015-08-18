package gp.e3.autheo.authentication.service.resources;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.UserBusiness;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authentication.service.resources.commons.HttpCommonResponses;
import gp.e3.autheo.authorization.domain.business.RoleBusiness;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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

import com.google.gson.Gson;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private final UserBusiness userBusiness;
	private final RoleBusiness roleBusiness;
	private final TokenBusiness tokenBusiness;

	public UserResource(UserBusiness userBusiness, RoleBusiness roleBusiness, TokenBusiness tokenBusiness) {
		
		this.userBusiness = userBusiness;
		this.roleBusiness = roleBusiness;
		this.tokenBusiness = tokenBusiness;
	}

	public static Map<String, String> getStringInJsonFormat(String key, String value) {

        Map<String, String> stringMessage = new Hashtable<String, String>();
        stringMessage.put(key, value);

        return stringMessage;
	}
	
	@POST
	public Response createUser(User user) {

		Response response = null;

		if (User.isAValidUser(user)) {

			User createdUser = userBusiness.createUser(user);
			
			if (createdUser != null) {
				
				tokenBusiness.generateAndSaveTokensForAnAPIUser(user);
				roleBusiness.addUserToRole(user.getUsername(), user.getRoleId());
				response = Response.status(201).entity(createdUser).build();
				
			} else {
				
				response = Response.status(500).entity(createdUser).build();
			}

		} else {

			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}

		return response;
	}

	@POST
	@Path("/{username}/tokens")
	public Response authenticateUser(@PathParam("username") String username, User user) {

		Response response = null;

		if ((user != null) && StringValidator.isValidString(user.getUsername()) && 
				StringValidator.isValidString(user.getPassword())) {

			try {

				if (userBusiness.authenticateUser(user.getUsername(), user.getPassword())) {
					
					// Get the complete user because its possible that the given user just
					// contains username and password.
					User completeUser = userBusiness.getUserByUsername(user.getUsername());
					Token token = tokenBusiness.generateToken(completeUser);
					response = Response.status(201).entity(token).build();

				} else {

                    Map<String, String> errorMessage = getStringInJsonFormat("error", "The user credentials are not valid.");
					response = Response.status(401).entity(errorMessage).build();
				}

			} catch (Exception e) {

                Map<String, String> errorMessage = getStringInJsonFormat("error", e.getMessage());
				response = Response.status(401).entity(errorMessage).build();
			}

		} else {

			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}

		return response;
	}

	@GET
	@Path("/{username}")
	public Response getUserByUsername(@PathParam("username") String username) {

		Response response = null;

		if (StringValidator.isValidString(username)) {

			User user = userBusiness.getUserByUsername(username);
			response = Response.status(200).entity(user).build();

		} else {

			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}

		return response;
	}

	@GET
	public Response getAllUsers() {

		List<User> users = userBusiness.getAllUsers();
		return Response.status(200).entity(users).build();
	}

	@PUT
	@Path("/{username}")
	public Response updateUser(@PathParam("username") String username, User updatedUser) {

		boolean updatedUserIsValid = (updatedUser != null) && StringValidator.isValidString(updatedUser.getName()) && 
				StringValidator.isValidString(updatedUser.getPassword());

		Response response = null;
		
		if (StringValidator.isValidString(username) && updatedUserIsValid) {

			boolean userWasUpdated = userBusiness.updateUser(username, updatedUser);
			
			if (userWasUpdated) {
				
				response = Response.status(200).build();
				
			} else {
				
				response = Response.status(500).build();
			}

		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}

	@DELETE
	@Path("/{username}")
	public Response deleteUser(@PathParam("username") String username) {
		
		Response response = null;
		
		if (StringValidator.isValidString(username)) {
			
			boolean userWasDeleted = userBusiness.deleteUser(username);
			
			if (userWasDeleted) {
				
				response = Response.status(200).build();
				
			} else {
				
				response = Response.status(500).build();
			}
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
}