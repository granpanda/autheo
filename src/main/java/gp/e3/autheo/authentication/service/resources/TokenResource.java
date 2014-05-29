package gp.e3.autheo.authentication.service.resources;

import java.sql.SQLException;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

	@POST
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
}