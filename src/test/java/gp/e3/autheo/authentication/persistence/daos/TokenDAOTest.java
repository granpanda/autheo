package gp.e3.autheo.authentication.persistence.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.infrastructure.exceptions.CheckedIllegalArgumentException;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
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
	private TokenDAO tokenDAO;

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
		
		tokenDAO = new TokenDAO(redisPoolMock);
	}

	@After
	public void tearDown() {
		
		redisMock = null;
		redisPoolMock = null;
		tokenDAO = null;
	}
	
	@Test
	public void testAddToken_OK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			String returnValue = "OK";
			
			Mockito.when(redisMock.set(token.getTokenValue(), token.toString())).thenReturn(returnValue);
			String addTokenAnswer = tokenDAO.addToken(token);
			
			assertEquals(returnValue, addTokenAnswer);
			
		} catch (TokenGenerationException | CheckedIllegalArgumentException e) {
			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAddToken_NOK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, null, user.getOrganizationId(), user.getRoleId());			
			tokenDAO.addToken(token);
			
			fail("The method should return an IllegalArgumentException because the given token is not valid.");
			
		} catch (TokenGenerationException | IllegalArgumentException | CheckedIllegalArgumentException e) {
			
			assertNotNull(e);
		}
	}
	
	@Test
	public void testGetToken_OK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			
			Mockito.when(redisMock.get(token.getTokenValue())).thenReturn(token.toString());			
			Token retrievedToken = tokenDAO.getToken(tokenValue);
			
			assertEquals(0, token.compareTo(retrievedToken));
			
		} catch (TokenGenerationException | CheckedIllegalArgumentException e) {
			
			fail(e.getMessage());
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
			tokenDAO.getToken(invalidUsername);
			
			fail("The method should throw an IllegalArgumentException because the given username was empty.");
			
		} catch (TokenGenerationException | IllegalArgumentException | CheckedIllegalArgumentException e) {
			
			assertNotNull(e);
		}
	}
	
	@Test
	public void testRemoveToken_OK() {
		
		String tokenValue = "tokenValue";
		
		long expectedValue = 1;
		Mockito.when(redisMock.del(tokenValue)).thenReturn(expectedValue);
		long numberOfRemovedKeys = tokenDAO.removeToken(tokenValue);
		
		assertEquals(expectedValue, numberOfRemovedKeys);
	}
	
	@Test
	public void testRemoveToken_NOK() {
		
		String tokenValue = "tokenValue";
		
		long expectedValue = 0;
		Mockito.when(redisMock.del(tokenValue)).thenReturn(expectedValue);
		long numberOfRemovedKeys = tokenDAO.removeToken(tokenValue);
		
		assertEquals(expectedValue, numberOfRemovedKeys);
	}
}