package gp.e3.autheo.authorization.infrastructure.validators;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

import org.apache.commons.validator.routines.UrlValidator;

public class E3UrlValidator {
	
	public static boolean isValidUrl(String url) {
	
		return StringValidator.isValidString(url) && UrlValidator.getInstance().isValid(url);
	}
	
	public static boolean urlsMatch(String url1, String url2) {
		
		
	}
}