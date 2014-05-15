package gp.e3.autheo.authentication.persistence.daos;

import static org.junit.Assert.*;
import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.persistence.daos.TokenCacheDAO;
import gp.e3.autheo.util.UserFactoryForTests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TokenDAOTest {
	
	private JedisPool redisPoolMock;
	private Jedis redisMock;

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		
		redisPoolMock = Mockito.mock(JedisPool.class);
		redisMock = Mockito.mock(Jedis.class);
		Mockito.when(redisPoolMock.getResource()).thenReturn(redisMock);
	}

	@After
	public void tearDown() {
		redisMock = null;
		redisPoolMock = null;
	}
	
	@Test
	public void testAddToken_OK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			String returnValue = "OK";
			
			Mockito.when(redisMock.set(token.getTokenValue(), token.toString())).thenReturn(returnValue);
			
			TokenCacheDAO tokenDao = new TokenCacheDAO(redisPoolMock);
			boolean addTokenAnswer = tokenDao.addTokenUsingTokenValueAsKey(token);
			
			assertEquals(true, addTokenAnswer);
			
		} catch (TokenGenerationException e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testAddToken_NOK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, null, user.getOrganizationId(), user.getRoleId());
			
			TokenCacheDAO tokenDao = new TokenCacheDAO(redisPoolMock);
			assertEquals(false, tokenDao.addTokenUsingTokenValueAsKey(token));
			
		} catch (TokenGenerationException e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetToken_OK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			
			Mockito.when(redisMock.get(token.getTokenValue())).thenReturn(token.toString());
			
			TokenCacheDAO tokenDao = new TokenCacheDAO(redisPoolMock);
			Token retrievedToken = tokenDao.getTokenByTokenValue(tokenValue);
			
			assertEquals(0, token.compareTo(retrievedToken));
			
		} catch (TokenGenerationException e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetToken_NOK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			
			Mockito.when(redisMock.get(token.getTokenValue())).thenReturn(token.toString());
			
			String invalidUsername = "";
			TokenCacheDAO tokenDao = new TokenCacheDAO(redisPoolMock);
			assertNull(tokenDao.getTokenByTokenValue(invalidUsername));
			
		} catch (TokenGenerationException | IllegalArgumentException e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}