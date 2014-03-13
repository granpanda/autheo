package gp.e3.autheo.authorization.infrastructure.validators;

import org.apache.commons.validator.routines.UrlValidator;

public class E3UrlValidator {
	
	public static boolean isValidUrl(String url) {
	
		return UrlValidator.getInstance().isValid(url);
	}
}