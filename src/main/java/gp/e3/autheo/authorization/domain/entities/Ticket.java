package gp.e3.autheo.authorization.domain.entities;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authorization.infrastructure.validators.E3UrlValidator;
import gp.e3.autheo.authorization.infrastructure.validators.HttpVerbValidator;

public class Ticket {
	
	private final String tokenValue;
	private final String username;
	private final String userOrganization;
	private final String userRoleName;
	
	private final String httpVerb;
	private final String requestedUrl;
	
	public Ticket(String tokenValue, String username, String userOrganization, 
			String userRoleName, String httpVerb, String requestedUrl) {
		
		this.tokenValue = tokenValue;
		this.username = username;
		this.userOrganization = userOrganization;
		this.userRoleName = userRoleName;
		this.httpVerb = httpVerb;
		this.requestedUrl = requestedUrl;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public String getUsername() {
		return username;
	}

	public String getUserOrganization() {
		return userOrganization;
	}

	public String getUserRoleName() {
		return userRoleName;
	}

	public String getHttpVerb() {
		return httpVerb;
	}

	public String getRequestedUrl() {
		return requestedUrl;
	}
	
	public static boolean isValidTicket(Ticket ticket) {
		
		return (StringValidator.isValidString(ticket.getTokenValue())) &&
				(StringValidator.isValidString(ticket.getUsername())) &&
				(StringValidator.isValidString(ticket.getUserOrganization())) &&
				(StringValidator.isValidString(ticket.getUserRoleName())) &&
				(HttpVerbValidator.isValidHttpVerb(ticket.getHttpVerb())) &&
				(E3UrlValidator.isValidUrl(ticket.getRequestedUrl()));
	}
}