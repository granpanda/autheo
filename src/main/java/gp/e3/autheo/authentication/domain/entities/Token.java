package gp.e3.autheo.authentication.domain.entities;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

public class Token implements Comparable<Token> {

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

	public static boolean isAValidToken(Token token) {

		return (token != null) && (StringValidator.isValidString(token.getUsername())) 
				&& (StringValidator.isValidString(token.getTokenValue()));
	}

	@Override
	public int compareTo(Token token) {

		int answer = 0;

		answer += this.username.compareTo(token.getUsername());
		answer += this.tokenValue.compareTo(token.getTokenValue());

		return answer;
	}
}