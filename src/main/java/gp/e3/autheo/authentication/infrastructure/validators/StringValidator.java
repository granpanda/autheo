package gp.e3.autheo.authentication.infrastructure.validators;

public class StringValidator {
	
	public static boolean isValidString(String string) {
	
		return (string != null) && (!string.isEmpty());
	}
}