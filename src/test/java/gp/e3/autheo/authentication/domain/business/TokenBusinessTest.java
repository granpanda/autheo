package gp.e3.autheo.authentication.domain.business;

import static org.junit.Assert.*;
import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
import gp.e3.autheo.util.UserFactoryForTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TokenBusinessTest {

	private TokenDAO tokenDaoMock;

	@Before
	public void setUp() {

		tokenDaoMock = Mockito.mock(TokenDAO.class);
	}

	@After
	public void tearDown() {

		tokenDaoMock = null;
	}

	@Test
	public void testGenerateToken_OK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String tokenValue = TokenFactory.getToken(user);
			Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());

			String returnValue = "OK";
			Mockito.when(tokenDaoMock.addToken(testToken)).thenReturn(returnValue);

			TokenBusiness tokenBusiness = new TokenBusiness(tokenDaoMock);
			Token generatedToken = tokenBusiness.generateToken(user);

			/*
			 * The username of both tokens should be the same, but the token values
			 * should be different because they are generated randomly.
			 */
			assertEquals(testToken.getUsername(), generatedToken.getUsername());
			assertNotEquals(testToken.getTokenValue(), generatedToken.getTokenValue());

		} catch (IllegalArgumentException | TokenGenerationException e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testGenerateToken_NOK() {

		try {

			User user = null;

			TokenBusiness tokenBusiness = new TokenBusiness(tokenDaoMock);
			tokenBusiness.generateToken(user);

			String errorMessage = "The method should throw an exception because the user give nby parameter was null";
			fail(errorMessage);

		} catch (IllegalArgumentException | TokenGenerationException e) {

			// Should get here.
			assertNotNull(e);
		}
	}

	@Test
	public void testGetTokenValue_OK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			
			Mockito.when(tokenDaoMock.getToken(user.getUsername())).thenReturn(testToken);
			TokenBusiness tokenBusiness = new TokenBusiness(tokenDaoMock);
			
			Token retrievedToken = tokenBusiness.getToken(user.getUsername());
			assertEquals(0, testToken.compareTo(retrievedToken));

		} catch (TokenGenerationException e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetTokenValue_NOK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();

		String tokenValue = "NULL";
		Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
		
		Mockito.when(tokenDaoMock.getToken(Mockito.anyString())).thenReturn(testToken);
		TokenBusiness tokenBusiness = new TokenBusiness(tokenDaoMock);
		
		String invalidUsername = null;
		
		try {
			
			tokenBusiness.getToken(invalidUsername);
			fail("The method should throw an exception because the username parameter was null.");
			
		} catch (Exception e) {
			
			assertNotNull(e);
		}
	}
}