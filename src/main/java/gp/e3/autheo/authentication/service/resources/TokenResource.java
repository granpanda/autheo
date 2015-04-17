package gp.e3.autheo.authentication.service.resources;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.exceptions.ValidDataException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/tokens")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TokenResource {
	
	private final TokenBusiness tokenBusiness;
	
	public TokenResource(TokenBusiness tokenBusiness) {
		this.tokenBusiness = tokenBusiness;
	}
	
	private String getStringInJsonFormat(String message) {

		Gson gson = new Gson();
		return gson.toJson(message);
	}
	
	@DELETE
	@Path("/{tokenValue}/cache")
	public Response removeUserAccessTokenFromCache(@PathParam("tokenValue") String tokenValue) {
		
		Response response = null;
		
		try {
			
			if(tokenBusiness.removeUserAccessToken(tokenValue)) {
				
				String msj = getStringInJsonFormat("Access token : " + tokenValue + " was successfully removed from the cache");
				response = Response.status(200).entity(msj).build();
				
			} else {
				
				String msj = getStringInJsonFormat("Access token : " + tokenValue + " was NOT removed from the cache because it was not found");
				response = Response.status(409).entity(msj).build();
			}
			
		} catch (ValidDataException e) {
			
			String msj = getStringInJsonFormat("Access token : " + tokenValue + " was NOT removed. The error was: " + e.getMessage());
			response = Response.status(409).entity(msj).build();
		}
		
		return response;
	}
}