package gp.e3.autheo.authorization.domain.entities;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authorization.infrastructure.validators.HttpVerbValidator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

public class Ticket {
	
	private final String tokenValue;
	private final String httpVerb;
	private final String requestedUrl;
	
	@JsonCreator
	public Ticket(@JsonProperty("tokenValue") String tokenValue, @JsonProperty("httpVerb") String httpVerb, 
			@JsonProperty("requestedUrl") String requestedUrl) {
		
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
	
	@Override
	public String toString() {
		
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public static boolean isValidTicket(Ticket ticket) {
		
		return (ticket != null) &&
				(StringValidator.isValidString(ticket.getTokenValue())) &&
				(HttpVerbValidator.isValidHttpVerb(ticket.getHttpVerb())) &&
				(StringValidator.isValidString(ticket.getRequestedUrl()));
	}
}