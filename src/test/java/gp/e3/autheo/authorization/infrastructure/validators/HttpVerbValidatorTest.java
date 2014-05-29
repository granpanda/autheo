package gp.e3.autheo.authorization.infrastructure.validators;

import static org.junit.Assert.*;

import org.junit.Test;

public class HttpVerbValidatorTest {
	
	@Test
	public void testIsValidHttpVerb_OK() {
		
		String httpVerb = "Options";
		assertTrue(HttpVerbValidator.isValidHttpVerb(httpVerb));
		
		httpVerb = "GeT";
		assertTrue(HttpVerbValidator.isValidHttpVerb(httpVerb));
		
		httpVerb = "post";
		assertTrue(HttpVerbValidator.isValidHttpVerb(httpVerb));
		
		httpVerb = "PUT";
		assertTrue(HttpVerbValidator.isValidHttpVerb(httpVerb));
		
		httpVerb = "DeLeTe";
		assertTrue(HttpVerbValidator.isValidHttpVerb(httpVerb));
		
		httpVerb = "HEAD";
		assertTrue(HttpVerbValidator.isValidHttpVerb(httpVerb));
		
		httpVerb = "PATCH";
		assertTrue(HttpVerbValidator.isValidHttpVerb(httpVerb));
	}
	
	@Test
	public void testIsValidHttpVerb_NOK() {
		
		String httpVerb = "";
		assertFalse(HttpVerbValidator.isValidHttpVerb(httpVerb));
		
		httpVerb = "Options1";
		assertFalse(HttpVerbValidator.isValidHttpVerb(httpVerb));
		
		httpVerb = "eT";
		assertFalse(HttpVerbValidator.isValidHttpVerb(httpVerb));
		
		httpVerb = "post2";
		assertFalse(HttpVerbValidator.isValidHttpVerb(httpVerb));
		
		httpVerb = "DeL";
		assertFalse(HttpVerbValidator.isValidHttpVerb(httpVerb));
	}
}