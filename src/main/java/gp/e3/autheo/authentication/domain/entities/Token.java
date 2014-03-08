package gp.e3.autheo.authentication.domain.entities;

public class Token {
	
	private final String username;
	private final String tokenValue;
	
	public Token(String username, String tokenValue) {
		
		this.username = username;
		this.tokenValue = tokenValue;
	}

	public String getUsername() {
		return username;
	}

	public String getTokenValue() {
		return tokenValue;
	}
}