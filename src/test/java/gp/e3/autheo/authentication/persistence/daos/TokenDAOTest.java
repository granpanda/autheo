package gp.e3.autheo.authentication.persistence.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
import gp.e3.autheo.util.UserFactoryForTests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import redis.clients.jedis.Jedis;

public class TokenDAOTest {
	
	private Jedis redisClientMock;

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		
		redisClientMock = Mockito.mock(Jedis.class);
	}

	@After
	public void tearDown() {
		
		redisClientMock = null;
	}
	
	@Test
	public void testAddToken_OK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			String returnValue = "OK";
			
			Mockito.when(redisClientMock.set(token.getUsername(), token.toString())).thenReturn(returnValue);
			
			TokenDAO tokenDao = new TokenDAO(redisClientMock);
			String addTokenAnswer = tokenDao.addToken(token);
			
			assertEquals(returnValue, addTokenAnswer);
			
		} catch (TokenGenerationException e) {
			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAddToken_NOK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, null, user.getOrganizationId(), user.getRoleId());
			
			TokenDAO tokenDao = new TokenDAO(redisClientMock);
			tokenDao.addToken(token);
			
			fail("The method should return an IllegalArgumentException because the given token is not valid.");
			
		} catch (TokenGenerationException | IllegalArgumentException e) {
			
			assertNotNull(e);
		}
	}
	
	@Test
	public void testGetToken_OK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			
			Mockito.when(redisClientMock.get(token.getUsername())).thenReturn(token.toString());
			
			TokenDAO tokenDao = new TokenDAO(redisClientMock);
			Token retrievedToken = tokenDao.getToken(user.getUsername());
			
			assertEquals(0, token.compareTo(retrievedToken));
			
		} catch (TokenGenerationException e) {
			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetToken_NOK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			
			Mockito.when(redisClientMock.get(token.getUsername())).thenReturn(token.toString());
			
			String invalidUsername = "";
			TokenDAO tokenDao = new TokenDAO(redisClientMock);
			tokenDao.getToken(invalidUsername);
			
			fail("The method should throw an IllegalArgumentException because the given username was empty.");
			
		} catch (TokenGenerationException | IllegalArgumentException e) {
			
			assertNotNull(e);
		}
	}
}