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
import gp.e3.autheo.authentication.domain.exceptions.ValidDataException;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenCacheDAO;
import gp.e3.autheo.util.TokenFactoryForTests;
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
	public void testGenerateAndSaveTokensForAnAPIUser_OK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		boolean tokensWereGeneratedAndSaved = tokenBusiness.generateAndSaveTokensForAnAPIUser(user);
		
		assertEquals(true, tokensWereGeneratedAndSaved);
	}
	
	@Test
	public void testGenerateAndSaveTokensForAnAPIUser_NOK() {
		
		User user = null;
		boolean tokensWereGeneratedAndSaved = tokenBusiness.generateAndSaveTokensForAnAPIUser(user);
		
		assertEquals(false, tokensWereGeneratedAndSaved);
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
	public void testGetAPIToken_OK_1() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

			Mockito.when(tokenCacheDaoMock.getTokenByTokenValue(tokenValue)).thenReturn(testToken);

			Token retrievedToken = tokenBusiness.getAPIToken(testToken.getTokenValue());
			assertEquals(0, testToken.compareTo(retrievedToken));

		} catch (TokenGenerationException e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testGetAPIToken_OK_2() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			Token testToken = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

			// First return null then return a valid Token object.
			Mockito.when(tokenCacheDaoMock.getTokenByTokenValue(tokenValue)).thenReturn(null).thenReturn(testToken);

			Token retrievedToken = tokenBusiness.getAPIToken(testToken.getTokenValue());
			assertEquals(0, testToken.compareTo(retrievedToken));

		} catch (TokenGenerationException e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetAPIToken_NOK() {

		String nullTokenValue = null;
		Token retrievedToken = tokenBusiness.getAPIToken(nullTokenValue);
		
		assertNull(retrievedToken);
	}
	
	@Test
	public void testGetModuleToken_OK_1() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		String organizationId = user.getOrganizationId();
		Token expectedToken = TokenFactoryForTests.getDefaultTestToken();
		
		Mockito.when(tokenCacheDaoMock.getTokenByOrganization(organizationId)).thenReturn(expectedToken);
		Token moduleToken = tokenBusiness.getModuleToken(organizationId);
		
		assertNotNull(moduleToken);
		assertEquals(0, expectedToken.compareTo(moduleToken));
	}
	
	@Test
	public void testGetModuleToken_OK_2() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		String organizationId = user.getOrganizationId();
		Token expectedToken = TokenFactoryForTests.getDefaultTestToken();
		
		Mockito.when(tokenCacheDaoMock.getTokenByOrganization(organizationId)).thenReturn(null).thenReturn(expectedToken);
		Token moduleToken = tokenBusiness.getModuleToken(organizationId);
		
		assertNotNull(moduleToken);
		assertEquals(0, expectedToken.compareTo(moduleToken));
	}
	
	@Test
	public void testGetModuleToken_NOK() {
		
		String nullOrganizationId = null;
		Token moduleToken = tokenBusiness.getModuleToken(nullOrganizationId);
		
		assertNull(moduleToken);
	}

	@Test
	public void testRemoveUserAccessToken_OK1(){

		boolean tokenRemoved = false;

		String tokenValue = "tokenTest";

		try {

			Mockito.when(tokenCacheDaoMock.removeUserAccessToken(tokenValue)).thenReturn(true);

			tokenRemoved = tokenBusiness.removeUserAccessToken(tokenValue);
			assertEquals(true, tokenRemoved);

			Mockito.when(tokenCacheDaoMock.removeUserAccessToken(tokenValue)).thenReturn(false);

			tokenRemoved = tokenBusiness.removeUserAccessToken(tokenValue);
			assertEquals(false, tokenRemoved);

		} catch (ValidDataException e) {
			fail("Exception Unexpected: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRemoveUserAccessToken_NOK1(){

		boolean tokenRemoved = false;

		String tokenValue = "";

		try {

			tokenRemoved = tokenBusiness.removeUserAccessToken(tokenValue);
			fail("Exception Expected");
			
		} catch (ValidDataException e) {
			assertEquals(false, tokenRemoved);
		}
	}
}