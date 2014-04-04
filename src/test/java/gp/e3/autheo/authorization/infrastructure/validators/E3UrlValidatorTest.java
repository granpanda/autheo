package gp.e3.autheo.authorization.infrastructure.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class E3UrlValidatorTest {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}
	
	@Test
	public void testUrlsMatch_OK() {
		
		String urlTemplate = "/api/users/*";
		String requestedUrl = "/api/users/julian";
		assertTrue(E3UrlValidator.urlsMatch(urlTemplate, requestedUrl));
		assertTrue(E3UrlValidator.urlsMatch(requestedUrl, urlTemplate)); // The other way around.
		
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
		
		urlTemplate = "www.google.com/docs";
		requestedUrl = "www.google.com/docs";
		assertTrue(E3UrlValidator.urlsMatch(urlTemplate, requestedUrl));
	}
	
	@Test
	public void testUrlsMatch_NOK() {
		
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