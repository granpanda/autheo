package gp.e3.autheo.authorization.infrastructure.validators;

public class HttpVerbValidator {
	
	private static final String[] VALID_HTTP_VERBS = { "OPTIONS", "GET", "POST", "PUT", "DELETE" };
	
	public static boolean isValidHttpVerb(String httpVerb) {
		
		boolean found = false;
		
		for (int i = 0; i < VALID_HTTP_VERBS.length && !found; i++) {
			
			String validVerb = VALID_HTTP_VERBS[i];
			found = validVerb.equalsIgnoreCase(httpVerb);
		}
		
		return found;
	}
}