package gp.e3.autheo.authorization.service;

import gp.e3.autheo.authentication.service.resources.commons.HttpCommonResponses;
import gp.e3.autheo.authorization.domain.business.TicketBusiness;
import gp.e3.autheo.authorization.domain.entities.Ticket;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthorizatorResource {
	
	private final TicketBusiness ticketBusiness;
	
	public AuthorizatorResource(TicketBusiness ticketBusiness) {
		
		this.ticketBusiness = ticketBusiness;
	}

	@PUT
	public Response isAuthorized(Ticket ticket) {
		
		Response response = null;
		
		if (Ticket.isValidTicket(ticket)) {
			
			if (ticketBusiness.tokenWasIssuedByUs(ticket)) {
				
				if (ticketBusiness.userIsAuthorized(ticket)) {
					
					response = Response.status(200).build();
					
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