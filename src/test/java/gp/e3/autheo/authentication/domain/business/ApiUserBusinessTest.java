package gp.e3.autheo.authentication.domain.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.entities.ApiUser;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.infrastructure.exceptions.CheckedIllegalArgumentException;
import gp.e3.autheo.authentication.persistence.daos.IApiUserDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.util.ApiUserFactoryForTests;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.security.sasl.AuthenticationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ApiUserBusinessTest {
		
	private TokenDAO tokenDAOMock;
	private IApiUserDAO apiUserDAOMock;
	private ApiUserBusiness apiUserBusiness;

	@Before
	public void setUp() {

		tokenDAOMock = Mockito.mock(TokenDAO.class);
		apiUserDAOMock = Mockito.mock(IApiUserDAO.class);
		apiUserBusiness = new ApiUserBusiness(apiUserDAOMock, tokenDAOMock);
	}

	@After
	public void tearDown() {

		tokenDAOMock = null;
		apiUserDAOMock = null;
		apiUserBusiness = null;
	}

	@Test
	public void testCreateApiUser_OK() {
		
		
		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		
		try {
		
			String tokenValue = TokenFactory.getToken(apiUser);
			Token token = new Token(tokenValue, apiUser.getUsername(), apiUser.getOrganizationId(), apiUser.getRoleId());
			
			String ok = "OK";
			Mockito.when(tokenDAOMock.addToken(token)).thenReturn(ok);
			
			ApiUser createdApiUser = apiUserBusiness.createApiUser(apiUser);
			assertEquals(0, apiUser.compareTo(createdApiUser));
			
		} catch (DuplicateIdException | IllegalArgumentException | CheckedIllegalArgumentException | TokenGenerationException e) {
			
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testCreateApiUser_NOK_1() {
		
		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		
		try {
			
			Mockito.doThrow(Exception.class).when(apiUserDAOMock).createApiUser(Mockito.anyString(), Mockito.anyString(), 
					Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
			
			apiUserBusiness.createApiUser(apiUser);
			fail("The method should threw an exception.");
			
		} catch (Exception e) {
			
			assertTrue(true);
		}
	}
	
	@Test
	public void testCreateApiUser_NOK_2() {
		
		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		
		try {
			
			String errorMessage = "The given token was not valid.";
			CheckedIllegalArgumentException checkedIllegalArgumentException = new CheckedIllegalArgumentException(errorMessage);
			Mockito.when(tokenDAOMock.addToken(Mockito.any(Token.class))).thenThrow(checkedIllegalArgumentException);
			
			apiUserBusiness.createApiUser(apiUser);
			fail("The method should threw an exception.");
			
		} catch (Exception e) {
			
			assertTrue(true);
		}
	}
	
	@Test
	public void testAuthenticateApiUser_OK() {
		
		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		try {
			
			String username = apiUser.getUsername();
			String password = apiUser.getPassword();
			
			String passwordHash = PasswordHandler.getPasswordHash(password);
			Mockito.when(apiUserDAOMock.getPasswordByUsername(username)).thenReturn(passwordHash);
			boolean isAuthenticated = apiUserBusiness.authenticateApiUser(username, password);
			
			assertEquals(true, isAuthenticated);
			
		} catch (AuthenticationException | NoSuchAlgorithmException | InvalidKeySpecException | CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testAuthenticateApiUser_NOK() {
		
		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		try {
			
			String username = apiUser.getUsername();
			String password = apiUser.getPassword();
			
			String incorrectPasswordHash = PasswordHandler.getPasswordHash(password + "123");
			Mockito.when(apiUserDAOMock.getPasswordByUsername(username)).thenReturn(incorrectPasswordHash);
			boolean isAuthenticated = apiUserBusiness.authenticateApiUser(username, password);
			
			assertEquals(false, isAuthenticated);
			
		} catch (AuthenticationException | NoSuchAlgorithmException | InvalidKeySpecException | CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetApiUserByUsername_OK() {
		
		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		String username = apiUser.getUsername();
		
		Mockito.when(apiUserDAOMock.getApiUserByUsername(username)).thenReturn(apiUser);
		ApiUser retrievedApiUser = apiUserBusiness.getApiUserByUsername(username);
		
		assertNotNull(retrievedApiUser);
		assertEquals(0, apiUser.compareTo(retrievedApiUser));
	}
	
	@Test
	public void testGetApiUserByUsername_NOK() {
		
		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		String username = apiUser.getUsername();
		
		Mockito.when(apiUserDAOMock.getApiUserByUsername(username)).thenReturn(null);
		ApiUser retrievedApiUser = apiUserBusiness.getApiUserByUsername(username);
		
		assertNull(retrievedApiUser);
	}
	
	@Test
	public void testGetApiUserByToken_OK() {
		
		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		String tokenValue = apiUser.getTokenValue();
		
		Mockito.when(apiUserDAOMock.getApiUserByToken(tokenValue)).thenReturn(apiUser);
		ApiUser retrievedApiUser = apiUserBusiness.getApiUserByToken(tokenValue);
		
		assertNotNull(retrievedApiUser);
		assertEquals(0, apiUser.compareTo(retrievedApiUser));
	}
	
	@Test
	public void testGetApiUserByToken_NOK() {
		
		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		String tokenValue = apiUser.getTokenValue();
		
		Mockito.when(apiUserDAOMock.getApiUserByToken(tokenValue)).thenReturn(null);
		ApiUser retrievedApiUser = apiUserBusiness.getApiUserByToken(tokenValue);
		
		assertNull(retrievedApiUser);
	}
}