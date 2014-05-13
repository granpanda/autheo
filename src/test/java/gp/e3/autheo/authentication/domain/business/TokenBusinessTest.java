package gp.e3.autheo.authentication.domain.business;

import static org.junit.Assert.*;
import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.infrastructure.exceptions.CheckedIllegalArgumentException;
import gp.e3.autheo.authentication.persistence.daos.IApiUserDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
import gp.e3.autheo.util.UserFactoryForTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TokenBusinessTest {

	private TokenDAO tokenDaoMock;
	private IApiUserDAO apiUserDAOMock;

	@Before
	public void setUp() {

		tokenDaoMock = Mockito.mock(TokenDAO.class);
		apiUserDAOMock = Mockito.mock(IApiUserDAO.class);
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

			TokenBusiness tokenBusiness = new TokenBusiness(tokenDaoMock, apiUserDAOMock);
			Token generatedToken = tokenBusiness.generateAndSaveTokenInCache(user);

			/*
			 * The username of both tokens should be the same, but the token values
			 * should be different because they are generated randomly.
			 */
			assertEquals(testToken.getUsername(), generatedToken.getUsername());
			assertNotEquals(testToken.getTokenValue(), generatedToken.getTokenValue());

		} catch (IllegalArgumentException | TokenGenerationException | CheckedIllegalArgumentException e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testGenerateToken_NOK() {

		try {

			User user = null;

			TokenBusiness tokenBusiness = new TokenBusiness(tokenDaoMock, apiUserDAOMock);
			tokenBusiness.generateAndSaveTokenInCache(user);

			String errorMessage = "The method should throw an exception because the user give nby parameter was null";
			fail(errorMessage);

		} catch (IllegalArgumentException | TokenGenerationException | CheckedIllegalArgumentException e) {

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
			
			Mockito.when(tokenDaoMock.getToken(tokenValue)).thenReturn(testToken);
			TokenBusiness tokenBusiness = new TokenBusiness(tokenDaoMock, apiUserDAOMock);
			
			Token retrievedToken = tokenBusiness.getToken(testToken.getTokenValue());
			assertEquals(0, testToken.compareTo(retrievedToken));

		} catch (TokenGenerationException | CheckedIllegalArgumentException e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetTokenValue_NOK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();

			String tokenValue = "NULL";
			Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			
			Mockito.when(tokenDaoMock.getToken(Mockito.anyString())).thenReturn(testToken);
			TokenBusiness tokenBusiness = new TokenBusiness(tokenDaoMock, apiUserDAOMock);
			
			String invalidTokenValue = null;
			
			tokenBusiness.getToken(invalidTokenValue);
			fail("The method should throw an exception because the username parameter was null.");
			
		} catch (Exception e) {
			
			assertNotNull(e);
		}
	}
}