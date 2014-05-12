package gp.e3.autheo.authorization.service;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.service.resources.commons.HttpCommonResponses;
import gp.e3.autheo.authorization.domain.business.TicketBusiness;
import gp.e3.autheo.authorization.domain.entities.Ticket;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/auth", description = "Authorization related operations")
public class TicketResource {
	
	private static Logger logger = LoggerFactory.getLogger(TicketResource.class);
	
	private final TicketBusiness ticketBusiness;
	
	public TicketResource(TicketBusiness ticketBusiness) {
		
		this.ticketBusiness = ticketBusiness;
	}

	@PUT
	@ApiOperation(value = "Ask if a user is allowed to execute a given action.")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "The user is unauthorized."),
			@ApiResponse(code = 403, message = "The user is forbidden.")
	})
	public Response isAuthorized(Ticket ticket) {
		
		logger.info("Received ticket: " + ticket);
		
		Response response = null;
		
		if (Ticket.isValidTicket(ticket)) {
			
			Token retrievedToken = ticketBusiness.tokenWasIssuedByUs(ticket);
			
			if (retrievedToken != null) {
				
				if (ticketBusiness.userIsAuthorized(ticket)) {
					
					response = Response.status(200).entity(retrievedToken).build();
					
				} else {
					
					// 403 Forbidden.
					String errorMessage = "You don't have permissions.";
					response = Response.status(403).entity(errorMessage).build();
				}
				
				
			} else {
			
				// 401 Unauthorized.
				String errorMessage = "You are not authenticated, please login.";
				response = Response.status(401).entity(errorMessage).build();
			}
			
		} else {
			
			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}
		
		return response;
	}
}