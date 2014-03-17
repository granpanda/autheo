package gp.e3.autheo.authorization.domain.entities;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authorization.infrastructure.validators.HttpVerbValidator;

public class Ticket {
	
	private final String tokenValue;
	private final String httpVerb;
	private final String requestedUrl;
	
	public Ticket(String tokenValue, String httpVerb, String requestedUrl) {
		
		this.tokenValue = tokenValue;
		this.httpVerb = httpVerb;
		this.requestedUrl = requestedUrl;
	}
	
	public String getTokenValue() {
		return tokenValue;
	}

	public String getHttpVerb() {
		return httpVerb;
	}

	public String getRequestedUrl() {
		return requestedUrl;
	}

	public static boolean isValidTicket(Ticket ticket) {
		
		return (StringValidator.isValidString(ticket.getTokenValue())) &&
				(HttpVerbValidator.isValidHttpVerb(ticket.getHttpVerb())) &&
				(StringValidator.isValidString(ticket.getRequestedUrl()));
	}
}