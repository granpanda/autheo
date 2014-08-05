package gp.e3.autheo.authentication.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

public class Token implements Comparable<Token> {

	private final String tokenValue;
	private final String username;
	private final String userOrganization;
	private final String userRole; 
	private final int tokenType;

	@JsonCreator
	public Token(@JsonProperty("tokenValue") String tokenValue, @JsonProperty("username") String username, 
				 @JsonProperty("userOrganization") String userOrganization, @JsonProperty("userRole") String userRole,
				 @JsonProperty("tokenType") int tokenType) {
		
		this.tokenValue = tokenValue;
		this.username = username;
		this.userOrganization = userOrganization;
		this.userRole = userRole;
		this.tokenType = tokenType;
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

	public int getTokenType() {
		return tokenType;
	}

	public static boolean isAValidToken(Token token) {

		return (token != null) && 
				StringValidator.isValidString(token.getTokenValue()) && 
				StringValidator.isValidString(token.getUsername()) && 
				StringValidator.isValidString(token.getUserOrganization()) && 
				StringValidator.isValidString(token.getUserRole()) && 
				(token.getTokenType() > 0);
	}
	
	public static Token buildTokenFromTokenToString(String tokenToString) {
		
		Gson gson = new Gson();
		return gson.fromJson(tokenToString, Token.class);
	}

	@Override
	public int compareTo(Token token) {

		int answer = 0;

		answer += this.tokenValue.compareTo(token.getTokenValue());
		answer += this.username.compareTo(token.getUsername());
		answer += this.userOrganization.compareTo(token.getUserOrganization());
		answer += this.userRole.compareTo(token.getUserRole());
		answer += (this.tokenType == token.getTokenType()? 0 : 1);

		return answer;
	}
	
	@Override
	public String toString() {
		
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}