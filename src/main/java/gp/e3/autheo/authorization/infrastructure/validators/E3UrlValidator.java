package gp.e3.autheo.authorization.infrastructure.validators;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

public class E3UrlValidator {

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

			int templateArrayLength = templateArray.length;
			
			if (templateArrayLength == requestedArray.length && templateArrayLength > 0) {
				
				if (templateArrayLength == 1) {
					
					match = templateArray[0].equalsIgnoreCase(requestedArray[0]);
					
				} else if (templateArrayLength > 1) {
					
					boolean partiallyMatch = true;
					
					for (int i = 0; i < templateArrayLength && partiallyMatch; i++) {

						String templateString = templateArray[i];
						String requestString = requestedArray[i];

						// Check both ways.
						partiallyMatch = matchUrlPart(anyCharacter, templateString, requestString) ||
								matchUrlPart(anyCharacter, requestString, templateString);
					}

					match = partiallyMatch;
				}
			}
		}

		return match;
	}
}