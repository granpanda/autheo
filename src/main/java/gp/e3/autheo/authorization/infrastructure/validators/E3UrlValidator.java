package gp.e3.autheo.authorization.infrastructure.validators;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

import org.apache.commons.validator.routines.UrlValidator;

public class E3UrlValidator {

	public static boolean isValidUrl(String url) {

		return StringValidator.isValidString(url) && UrlValidator.getInstance().isValid(url);
	}

	private static boolean matchUrlPart(String anyCharacter, String templateString, String requestString) {

		boolean partiallyMatch = false;

		if (templateString.equals(anyCharacter)) {

			partiallyMatch = StringValidator.isValidString(requestString);

		} else {

			partiallyMatch = templateString.equalsIgnoreCase(requestString);
		}

		return partiallyMatch;
	}

	private static String removeInitialSlash(String url) {

		String correctedUrl = "";

		char slash = '/';

		if (url.charAt(0) == slash) {

			correctedUrl = url.substring(1);
			
		} else {
			
			correctedUrl = url;
		}

		return correctedUrl;
	}
	
	private static String removeFinalSlash(String url) {

		String correctedUrl = "";

		char slash = '/';
		int urlLength = url.length();
		
		if (url.charAt(urlLength - 1) == slash) {

			correctedUrl = url.substring(0, urlLength - 1);
			
		} else {
			
			correctedUrl = url;
		}
		
		return correctedUrl;
	}
	
	private static String removeInitialAndFinalSlash(String url) {
		
		return removeFinalSlash(removeInitialSlash(url));
	}

	public static boolean urlsMatch(String urlTemplate, String requestedUrl) {

		boolean match = false;

		String splitCharacter = "/";
		String anyCharacter = "*";

		if (StringValidator.isValidString(urlTemplate) && StringValidator.isValidString(requestedUrl)) {
			
			urlTemplate = removeInitialAndFinalSlash(urlTemplate);
			requestedUrl = removeInitialAndFinalSlash(requestedUrl);

			String[] templateArray = urlTemplate.split(splitCharacter);
			String[] requestedArray = requestedUrl.split(splitCharacter);

			boolean partiallyMatch = true;

			if (templateArray.length == requestedArray.length) {

				for (int i = 0; i < templateArray.length && partiallyMatch; i++) {

					String templateString = templateArray[i];
					String requestString = requestedArray[i];

					partiallyMatch = matchUrlPart(anyCharacter, templateString, requestString);
				}

				match = partiallyMatch;
			}
		}

		return match;
	}
}