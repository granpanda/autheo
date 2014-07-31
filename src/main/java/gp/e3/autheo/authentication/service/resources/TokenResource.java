package gp.e3.autheo.authentication.service.resources;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.exceptions.ValidDataException;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tokens")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TokenResource {
	
	private final TokenBusiness tokenBusiness;
	
	public TokenResource(TokenBusiness tokenBusiness) {
		this.tokenBusiness = tokenBusiness;
	}

	@PUT
	public Response updateTokensCache() {
		
		Response response = null;
		
		try {
			
			tokenBusiness.updateTokensCache();
			String message = "The tokens cache was successfully updated.";
			response = Response.status(200).entity(message).build();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			String errorMessage = "The tokens cache was not updated. There was an error in the db or in the cache.";
			response = Response.status(500).entity(errorMessage).build();
		}
		
		return response;
	}
	
	@DELETE
	@Path("/{tokenValue}/cache")
	public Response removeUserAccessTokenFromCache(@PathParam("tokenValue") String tokenValue){
		Response response = null;
		String msj = "";
		
		try {
			if(tokenBusiness.removeUserAccessToken(tokenValue)){
				msj = "Access token : " + tokenValue + " was successfully removed from the cache";
				response = Response.status(200).entity(msj).build();			
			} else {
				msj = "Access token : " + tokenValue + " was NOT removed from the cache because it was not found";
				response = Response.status(409).entity(msj).build();
			}
		} catch (ValidDataException e) {
			msj = "Access token : " + tokenValue + " was NOT removed. The error was: " + e.getMessage();
			response = Response.status(409).entity(msj).build();
		}
		
		return response;
	}
}