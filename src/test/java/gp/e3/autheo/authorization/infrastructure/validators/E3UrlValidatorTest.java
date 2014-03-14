package gp.e3.autheo.authorization.infrastructure.validators;

import static org.junit.Assert.*;

import org.junit.Test;

public class E3UrlValidatorTest {

	@Test
	public void tesUrlsMatch_OK() {
		
		String urlTemplate = "/api/users/*";
		String requestedUrl = "/api/users/julian";
		assertTrue(E3UrlValidator.urlsMatch(urlTemplate, requestedUrl));
		
		// Trailing slashes are omited.
		urlTemplate = "/api/users/*";
		requestedUrl = "/api/users/julian/";
		assertTrue(E3UrlValidator.urlsMatch(urlTemplate, requestedUrl));
		
		urlTemplate = "/api/users/*/hello";
		requestedUrl = "/api/users/julian/hello";
		assertTrue(E3UrlValidator.urlsMatch(urlTemplate, requestedUrl));
		
		urlTemplate = "/api/orders/*/payment/*/payment-mode";
		requestedUrl = "/api/orders/123/payment/credit-card/payment-mode";
		assertTrue(E3UrlValidator.urlsMatch(urlTemplate, requestedUrl));
	}
	
	@Test
	public void tesUrlsMatch_NOK() {
		
		String urlTemplate = "/api/users/*";
		String requestedUrl = "";
		assertFalse(E3UrlValidator.urlsMatch(urlTemplate, requestedUrl));
		
		urlTemplate = "";
		requestedUrl = "/api/users/julian";
		assertFalse(E3UrlValidator.urlsMatch(urlTemplate, requestedUrl));
		
		urlTemplate = "/api/users/*/hello";
		requestedUrl = "/api/users/julian/not-hello";
		assertFalse(E3UrlValidator.urlsMatch(urlTemplate, requestedUrl));
		
		urlTemplate = "/api/users/*/hello////";
		requestedUrl = "/api/users/julian/not-hello";
		assertFalse(E3UrlValidator.urlsMatch(urlTemplate, requestedUrl));
	}
}