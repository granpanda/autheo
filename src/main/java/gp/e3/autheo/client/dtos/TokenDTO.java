package gp.e3.autheo.client.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

public class TokenDTO {

	private final String tokenValue;
	private final String username;
	private final String userOrganization;
	private final String userRole; 

	@JsonCreator
	public TokenDTO(@JsonProperty("tokenValue") String tokenValue, @JsonProperty("username") String username, 
			@JsonProperty("userOrganization") String userOrganization, @JsonProperty("userRole") String userRole) {

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

		Gson gson = new Gson();
		return gson.fromJson(tokenToString, Token.class);
	}

	public static TokenDTO buildTokenDTOFromTokenToString(String tokenToString) {

		Gson gson = new Gson();
		return gson.fromJson(tokenToString, TokenDTO.class);
	}

	@Override
	public String toString() {

		Gson gson = new Gson();
		return gson.toJson(this);
	}
}