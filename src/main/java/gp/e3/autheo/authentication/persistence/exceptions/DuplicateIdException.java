package gp.e3.autheo.authentication.persistence.exceptions;

public class DuplicateIdException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DuplicateIdException(String message) {
		
		super(message);
	}
}