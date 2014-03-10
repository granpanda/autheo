package gp.e3.autheo.authentication.domain.entities;

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

	@Override
	public int compareTo(Token token) {
		
		int answer = 0;
		
		answer += this.username.compareTo(token.getUsername());
		answer += this.tokenValue.compareTo(token.getTokenValue());
		
		return answer;
	}
}