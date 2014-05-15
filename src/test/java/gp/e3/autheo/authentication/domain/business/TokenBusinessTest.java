package gp.e3.autheo.authentication.domain.business;

import static org.junit.Assert.*;
import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.persistence.daos.ITokenDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenCacheDAO;
import gp.e3.autheo.util.UserFactoryForTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TokenBusinessTest {

	private ITokenDAO tokenDAOMock;
	private TokenCacheDAO tokenCacheDaoMock;
	private TokenBusiness tokenBusiness;

	@Before
	public void setUp() {

		tokenDAOMock = Mockito.mock(ITokenDAO.class);
		tokenCacheDaoMock = Mockito.mock(TokenCacheDAO.class);
		tokenBusiness = new TokenBusiness(tokenDAOMock, tokenCacheDaoMock);
	}

	@After
	public void tearDown() {

		tokenDAOMock = null;
		tokenCacheDaoMock = null;
		tokenBusiness = null;
	}

	@Test
	public void testGenerateToken_OK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String tokenValue = TokenFactory.getToken(user);
			Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());

			boolean returnValue = true;
			Mockito.when(tokenCacheDaoMock.addTokenUsingTokenValueAsKey(testToken)).thenReturn(returnValue);

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
			
			Mockito.when(tokenCacheDaoMock.getTokenByTokenValue(tokenValue)).thenReturn(testToken);
			
			Token retrievedToken = tokenBusiness.getToken(testToken.getTokenValue());
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
		Mockito.when(tokenCacheDaoMock.getTokenByTokenValue(Mockito.anyString())).thenReturn(testToken);
		
		String invalidTokenValue = null;
		
		try {
			
			assertNull(tokenBusiness.getToken(invalidTokenValue));
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}