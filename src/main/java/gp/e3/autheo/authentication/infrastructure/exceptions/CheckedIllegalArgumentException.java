package gp.e3.autheo.authentication.infrastructure.exceptions;

public class CheckedIllegalArgumentException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CheckedIllegalArgumentException(String errorMessage) {
		super(errorMessage);
	}
}