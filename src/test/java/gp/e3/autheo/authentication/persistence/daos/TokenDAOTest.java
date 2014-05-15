package gp.e3.autheo.authentication.persistence.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.util.TokenFactoryForTests;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

public class TokenDAOTest {

	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static DBI dbi;
	private static Handle handle;
	private static ITokenDAO tokenDAO;

	@BeforeClass
	public static void setUpClass() {

		dbi = new DBI(H2_IN_MEMORY_DB);
		handle = dbi.open();
		tokenDAO = handle.attach(ITokenDAO.class);
	}

	@AfterClass
	public static void tearDownClass() {

		handle.close();
		dbi.close(tokenDAO);
		tokenDAO = null;
		dbi = null;
	}

	@Before
	public void setUp() {

		tokenDAO.createTokensTableIfNotExists();
	}

	@After
	public void tearDown() {

		handle.execute("DROP TABLE tokens");
	}

	@Test
	public void testCountTokensTableRows_OK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows());

			Token token = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(token.getTokenValue(), token.getUsername(), token.getUserOrganization(), token.getUserRole());

			assertEquals(1, tokenDAO.countTokensTableRows());

			Token token2 = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.createToken(token2.getTokenValue(), token2.getUsername(), token2.getUserOrganization(), token2.getUserRole());

			assertEquals(2, tokenDAO.countTokensTableRows());

		} catch (UnableToExecuteStatementException e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testCountTokensTableRows_NOK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows());

			Token token = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(token.getTokenValue(), token.getUsername(), token.getUserOrganization(), token.getUserRole());

			assertEquals(1, tokenDAO.countTokensTableRows());

			// Add again the same token
			tokenDAO.createToken(token.getTokenValue(), token.getUsername(), token.getUserOrganization(), token.getUserRole());
			fail("Should threw an exception because the PK was duplicated.");

		} catch (UnableToExecuteStatementException e) {

			assertEquals(1, tokenDAO.countTokensTableRows());
		}
	}

	@Test
	public void testCreateToken_OK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows());

			Token token = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(token.getTokenValue(), token.getUsername(), token.getUserOrganization(), token.getUserRole());

			assertEquals(1, tokenDAO.countTokensTableRows());

			Token token2 = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.createToken(token2.getTokenValue(), token2.getUsername(), token2.getUserOrganization(), token2.getUserRole());

			assertEquals(2, tokenDAO.countTokensTableRows());

		} catch (UnableToExecuteStatementException e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testCreateToken_NOK() {
		
		try {

			assertEquals(0, tokenDAO.countTokensTableRows());

			Token token = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(token.getTokenValue(), token.getUsername(), token.getUserOrganization(), token.getUserRole());

			assertEquals(1, tokenDAO.countTokensTableRows());

			// Add again the same token
			tokenDAO.createToken(token.getTokenValue(), token.getUsername(), token.getUserOrganization(), token.getUserRole());
			fail("Should threw an exception because the PK was duplicated.");

		} catch (UnableToExecuteStatementException e) {

			assertEquals(1, tokenDAO.countTokensTableRows());
		}
	}
	
	@Test
	public void testGetAllTokens_OK() {
		
		try {

			assertEquals(0, tokenDAO.countTokensTableRows());

			Token token = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(token.getTokenValue(), token.getUsername(), token.getUserOrganization(), token.getUserRole());

			assertEquals(1, tokenDAO.countTokensTableRows());

			Token token2 = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.createToken(token2.getTokenValue(), token2.getUsername(), token2.getUserOrganization(), token2.getUserRole());

			assertEquals(2, tokenDAO.countTokensTableRows());
			
			List<Token> tokens = tokenDAO.getAllTokens();
			
			assertEquals(2, tokenDAO.countTokensTableRows());
			assertEquals(tokenDAO.countTokensTableRows(), tokens.size());

		} catch (UnableToExecuteStatementException e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetAllTokens_NOK() {
		
		try {

			assertEquals(0, tokenDAO.countTokensTableRows());
			
			List<Token> tokens = tokenDAO.getAllTokens();
			assertEquals(tokenDAO.countTokensTableRows(), tokens.size());

		} catch (UnableToExecuteStatementException e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testUpdateTokenByTokenValue_OK() {
		
		try {

			assertEquals(0, tokenDAO.countTokensTableRows());

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(firstToken.getTokenValue(), firstToken.getUsername(), firstToken.getUserOrganization(), firstToken.getUserRole());

			assertEquals(1, tokenDAO.countTokensTableRows());
			
			List<Token> tokens = tokenDAO.getAllTokens();
			
			assertEquals(1, tokenDAO.countTokensTableRows());
			assertEquals(tokenDAO.countTokensTableRows(), tokens.size());
			
			Token createdToken = tokens.get(0);
			assertEquals(0, firstToken.compareTo(createdToken));
			
			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.updateTokenByTokenValue(createdToken.getTokenValue(), secondToken.getTokenValue(), secondToken.getUsername(), secondToken.getUserOrganization(), 
					secondToken.getUserRole());
			
			List<Token> updatedTokenList = tokenDAO.getAllTokens();
			assertEquals(1, tokenDAO.countTokensTableRows());
			assertEquals(tokenDAO.countTokensTableRows(), updatedTokenList.size());
			
			Token updatedToken = updatedTokenList.get(0);
			assertEquals(0, secondToken.compareTo(updatedToken));

		} catch (UnableToExecuteStatementException e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testUpdateTokenByTokenValue_NOK() {
		
		try {

			assertEquals(0, tokenDAO.countTokensTableRows());

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(firstToken.getTokenValue(), firstToken.getUsername(), firstToken.getUserOrganization(), firstToken.getUserRole());

			assertEquals(1, tokenDAO.countTokensTableRows());
			
			List<Token> tokens = tokenDAO.getAllTokens();
			
			assertEquals(1, tokenDAO.countTokensTableRows());
			assertEquals(tokenDAO.countTokensTableRows(), tokens.size());
			
			Token createdToken = tokens.get(0);
			assertEquals(0, firstToken.compareTo(createdToken));
			
			String unknownTokenValue = "unknownTokenValue";
			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.updateTokenByTokenValue(unknownTokenValue, secondToken.getTokenValue(), secondToken.getUsername(), secondToken.getUserOrganization(), 
					secondToken.getUserRole());
			
			List<Token> updatedTokenList = tokenDAO.getAllTokens();
			assertEquals(1, tokenDAO.countTokensTableRows());
			assertEquals(tokenDAO.countTokensTableRows(), updatedTokenList.size());
			
			// The token was not updated because the given token value was not into the db.
			Token updatedToken = updatedTokenList.get(0);
			assertEquals(0, firstToken.compareTo(updatedToken));

		} catch (UnableToExecuteStatementException e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testDeleteTokenByTokenValue_OK() {
		
		assertEquals(0, tokenDAO.countTokensTableRows());

		Token firstToken = TokenFactoryForTests.getDefaultTestToken();
		tokenDAO.createToken(firstToken.getTokenValue(), firstToken.getUsername(), firstToken.getUserOrganization(), firstToken.getUserRole());

		assertEquals(1, tokenDAO.countTokensTableRows());

		Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
		tokenDAO.createToken(secondToken.getTokenValue(), secondToken.getUsername(), secondToken.getUserOrganization(), secondToken.getUserRole());

		assertEquals(2, tokenDAO.countTokensTableRows());
		
		List<Token> tokens = tokenDAO.getAllTokens();
		
		assertEquals(2, tokenDAO.countTokensTableRows());
		assertEquals(tokenDAO.countTokensTableRows(), tokens.size());
		
		tokenDAO.deleteTokenByTokenValue(firstToken.getTokenValue());
		assertEquals(1, tokenDAO.countTokensTableRows());
		
		tokenDAO.deleteTokenByTokenValue(secondToken.getTokenValue());
		assertEquals(0, tokenDAO.countTokensTableRows());
	}
	
	@Test
	public void testDeleteTokenByTokenValue_NOK() {
		
		assertEquals(0, tokenDAO.countTokensTableRows());

		Token firstToken = TokenFactoryForTests.getDefaultTestToken();
		tokenDAO.createToken(firstToken.getTokenValue(), firstToken.getUsername(), firstToken.getUserOrganization(), firstToken.getUserRole());

		assertEquals(1, tokenDAO.countTokensTableRows());

		Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
		tokenDAO.createToken(secondToken.getTokenValue(), secondToken.getUsername(), secondToken.getUserOrganization(), secondToken.getUserRole());

		assertEquals(2, tokenDAO.countTokensTableRows());
		
		List<Token> tokens = tokenDAO.getAllTokens();
		
		assertEquals(2, tokenDAO.countTokensTableRows());
		assertEquals(tokenDAO.countTokensTableRows(), tokens.size());
		
		String unknownTokenValue = "unknownTokenValue";
		tokenDAO.deleteTokenByTokenValue(unknownTokenValue);
		assertEquals(2, tokenDAO.countTokensTableRows());
	}
	
	@Test
	public void testDeleteTokenByUsername_OK() {
		
		assertEquals(0, tokenDAO.countTokensTableRows());

		Token firstToken = TokenFactoryForTests.getDefaultTestToken();
		tokenDAO.createToken(firstToken.getTokenValue(), firstToken.getUsername(), firstToken.getUserOrganization(), firstToken.getUserRole());

		assertEquals(1, tokenDAO.countTokensTableRows());

		Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
		tokenDAO.createToken(secondToken.getTokenValue(), secondToken.getUsername(), secondToken.getUserOrganization(), secondToken.getUserRole());

		assertEquals(2, tokenDAO.countTokensTableRows());
		
		List<Token> tokens = tokenDAO.getAllTokens();
		
		assertEquals(2, tokenDAO.countTokensTableRows());
		assertEquals(tokenDAO.countTokensTableRows(), tokens.size());
		
		tokenDAO.deleteTokenByUsername(firstToken.getUsername());
		assertEquals(1, tokenDAO.countTokensTableRows());
		
		tokenDAO.deleteTokenByUsername(secondToken.getUsername());
		assertEquals(0, tokenDAO.countTokensTableRows());
	}
	
	@Test
	public void testDeleteTokenByUsername_NOK() {
		
		assertEquals(0, tokenDAO.countTokensTableRows());

		Token firstToken = TokenFactoryForTests.getDefaultTestToken();
		tokenDAO.createToken(firstToken.getTokenValue(), firstToken.getUsername(), firstToken.getUserOrganization(), firstToken.getUserRole());

		assertEquals(1, tokenDAO.countTokensTableRows());

		Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
		tokenDAO.createToken(secondToken.getTokenValue(), secondToken.getUsername(), secondToken.getUserOrganization(), secondToken.getUserRole());

		assertEquals(2, tokenDAO.countTokensTableRows());
		
		List<Token> tokens = tokenDAO.getAllTokens();
		
		assertEquals(2, tokenDAO.countTokensTableRows());
		assertEquals(tokenDAO.countTokensTableRows(), tokens.size());
		
		String unknownUsername = "unknownUsername";
		tokenDAO.deleteTokenByUsername(unknownUsername);
		assertEquals(2, tokenDAO.countTokensTableRows());
	}
}