package gp.e3.autheo.client;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

public class TokenDTO {
	
	public static final String ATTRIBUTE_SEPATATOR = ":";
	public static final String TOKEN_SEPATATOR = ";";

	private final String tokenValue;
	private final String username;
	private final String userOrganization;
	private final String userRole; 

	public TokenDTO(String tokenValue, String username, String userOrganization, String userRole) {
		
		this.tokenValue = tokenValue;
		this.username = username;
		this.userOrganization = userOrganization;
		this.userRole = userRole;
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

	public String getUserRole() {
		return userRole;
	}

	public static boolean isAValidToken(Token token) {

		return (token != null) && 
				StringValidator.isValidString(token.getTokenValue()) && 
				StringValidator.isValidString(token.getUsername()) && 
				StringValidator.isValidString(token.getUserOrganization()) && 
				StringValidator.isValidString(token.getUserRole());
	}
	
	public static Token buildTokenFromTokenToString(String tokenToString) {
		
		String[] tokenAttributes = tokenToString.split(ATTRIBUTE_SEPATATOR);
		return new Token(tokenAttributes[0], tokenAttributes[1], tokenAttributes[2], tokenAttributes[3]);
	}
	
	@Override
	public String toString() {
		
		return tokenValue + ATTRIBUTE_SEPATATOR + username + ATTRIBUTE_SEPATATOR + userOrganization
				+ ATTRIBUTE_SEPATATOR + userRole;
	}
}