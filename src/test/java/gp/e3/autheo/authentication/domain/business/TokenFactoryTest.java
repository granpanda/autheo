package gp.e3.autheo.authentication.domain.business;

import static org.junit.Assert.*;
import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.util.UserFactoryForTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokenFactoryTest {
	
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGetToken_OK() {
		
		try {
			
			User defaultUser = UserFactoryForTests.getDefaultTestUser();
			String tokenFromDefaultUser = TokenFactory.getToken(defaultUser);
			
			User user2 = UserFactoryForTests.getDefaultTestUser(2);
			String tokenFromUser2 = TokenFactory.getToken(user2);
			
			assertNotEquals(tokenFromDefaultUser, tokenFromUser2);
			
			String defaultUserSecondToken = TokenFactory.getToken(defaultUser);
			
			/*
			 * Not even the tokens generated from the same user should be the same
			 * because the tokens are generated randomly.
			 */
			assertNotEquals(tokenFromDefaultUser, defaultUserSecondToken);
			
		} catch (TokenGenerationException e) {
			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetToken_NOK() {
		
		try {
			
			User defaultUser = null;
			TokenFactory.getToken(defaultUser);
			
			fail("The method should throw an exception because the user was null.");
			
		} catch (TokenGenerationException | IllegalArgumentException e) {
			
			assertNotNull(e);
		}
	}
}