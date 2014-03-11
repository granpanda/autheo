package gp.e3.autheo.authentication.domain.exceptions;

public class TokenGenerationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public TokenGenerationException(String errorMessage) {
		
		super(errorMessage);
	}
}