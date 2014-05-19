package gp.e3.autheo.authentication.domain.business;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.business.constants.TokenTypes;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenCacheDAO;
import gp.e3.autheo.util.UserFactoryForTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TokenBusinessTest {

	private Connection dbConnectionMock;
	private BasicDataSource dataSourceMock;
	
	private TokenDAO tokenDAOMock;
	private TokenCacheDAO tokenCacheDaoMock;
	private TokenBusiness tokenBusiness;

	@Before
	public void setUp() {

		dbConnectionMock = Mockito.mock(Connection.class);
		dataSourceMock = Mockito.mock(BasicDataSource.class);
		
		try {
			Mockito.when(dataSourceMock.getConnection()).thenReturn(dbConnectionMock);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		tokenDAOMock = Mockito.mock(TokenDAO.class);
		tokenCacheDaoMock = Mockito.mock(TokenCacheDAO.class);
		tokenBusiness = new TokenBusiness(dataSourceMock, tokenDAOMock, tokenCacheDaoMock);
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
			Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

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
	public void testGetTokenValue_OK_1() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());
			
			Mockito.when(tokenCacheDaoMock.getTokenByTokenValue(tokenValue)).thenReturn(testToken);
			
			Token retrievedToken = tokenBusiness.getToken(testToken.getTokenValue());
			assertEquals(0, testToken.compareTo(retrievedToken));

		} catch (TokenGenerationException e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetTokenValue_OK_2() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());
			
			// First return null then return a valid Token object.
			Mockito.when(tokenCacheDaoMock.getTokenByTokenValue(tokenValue)).thenReturn(null).thenReturn(testToken);
			
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
		Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());
		Mockito.when(tokenCacheDaoMock.getTokenByTokenValue(Mockito.anyString())).thenReturn(testToken);
		
		String invalidTokenValue = null;
		
		try {
			
			assertNull(tokenBusiness.getToken(invalidTokenValue));
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}