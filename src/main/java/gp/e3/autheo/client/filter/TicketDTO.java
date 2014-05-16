package gp.e3.autheo.client.filter;

public class TicketDTO {
	
	public static final String ATTRIBUTE_SPLIT = ":";
	public static final String PERMISSION_SPLIT = ";";
	
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
		
		return tokenValue + ATTRIBUTE_SPLIT + httpVerb + ATTRIBUTE_SPLIT + requestedUrl;
	}
}