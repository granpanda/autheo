package gp.e3.autheo.authentication.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import gp.e3.autheo.authentication.infrastructure.exceptions.CheckedIllegalArgumentException;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

public class Token implements Comparable<Token> {
	
	public static final String ATTRIBUTE_SEPATATOR = ":";
	public static final String TOKEN_SEPATATOR = ";";

	private final String tokenValue;
	private final String username;
	private final String userOrganization;
	private final String userRole; 

	@JsonCreator
	public Token(@JsonProperty("tokenValue") String tokenValue, @JsonProperty("username") String username, 
				 @JsonProperty("userOrganization") String userOrganization, 
				 @JsonProperty("userRole") String userRole) {
		
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
	
	public static Token buildTokenFromTokenToString(String tokenToString) throws CheckedIllegalArgumentException {
		
		try {
			
			String[] tokenAttributes = tokenToString.split(ATTRIBUTE_SEPATATOR);
			return new Token(tokenAttributes[0], tokenAttributes[1], tokenAttributes[2], tokenAttributes[3]);
			
		} catch (Exception e) {
			
			String errorMessage = "The given tokenToString argument does not follow the expected pattern.";
			throw new CheckedIllegalArgumentException(errorMessage);
		}
	}

	@Override
	public int compareTo(Token token) {

		int answer = 0;

		answer += this.tokenValue.compareTo(token.getTokenValue());
		answer += this.username.compareTo(token.getUsername());
		answer += this.userOrganization.compareTo(token.getUserOrganization());
		answer += this.userRole.compareTo(token.getUserRole());

		return answer;
	}
	
	@Override
	public String toString() {
		
		return tokenValue + ATTRIBUTE_SEPATATOR + username + ATTRIBUTE_SEPATATOR + userOrganization
				+ ATTRIBUTE_SEPATATOR + userRole;
	}
}