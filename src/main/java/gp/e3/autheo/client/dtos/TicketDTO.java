package gp.e3.autheo.client.dtos;

import com.google.gson.Gson;

public class TicketDTO {
	
	private final String tokenValue;
	private final String httpVerb;
	private final String requestedUrl;
	
	public TicketDTO(String tokenValue, String httpVerb, String requestedUrl) {
		
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
}