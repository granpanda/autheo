package gp.e3.autheo.authentication.persistence.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.util.TokenFactoryForTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TokenDAOTest {

	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static Connection dbConnection;
	private static TokenDAO tokenDAO;

	@BeforeClass
	public static void setUpClass() {

		try {
			dbConnection = DriverManager.getConnection(H2_IN_MEMORY_DB);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		tokenDAO = new TokenDAO();
	}

	@AfterClass
	public static void tearDownClass() {

		try {
			dbConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		dbConnection = null;
		tokenDAO = null;
	}

	@Before
	public void setUp() {

		tokenDAO.createTokensTableIfNotExists(dbConnection);
	}

	private void logUnexpectedException(SQLException e) {

		e.printStackTrace();
		fail("Unexpected exception: " + e.getMessage());
	}

	@After
	public void tearDown() {

		String dropTableSQL = "DROP TABLE tokens";

		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(dropTableSQL);
			prepareStatement.executeUpdate();
			
		} catch (SQLException e) {
			
			logUnexpectedException(e);
		}
	}

	@Test
	public void testCountTokensTableRows_OK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, firstToken);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.createToken(dbConnection, secondToken);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));

		} catch (SQLException e) {

			logUnexpectedException(e);
		}
	}

	@Test
	public void testCountTokensTableRows_NOK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token token = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, token);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			// Add again the same token
			tokenDAO.createToken(dbConnection, token);
			fail("Should threw an exception because the PK was duplicated.");

		} catch (SQLException e) {

			try {
				assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));
			} catch (SQLException e1) {
				fail("Unexpected exception: " + e1.getMessage());
			}
		}
	}

	@Test
	public void testCreateToken_OK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, firstToken);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.createToken(dbConnection, secondToken);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));

		} catch (SQLException e) {

			logUnexpectedException(e);
		}
	}

	@Test
	public void testCreateToken_NOK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token token = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, token);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			// Add again the same token
			tokenDAO.createToken(dbConnection, token);
			fail("Should threw an exception because the PK was duplicated.");

		} catch (SQLException e) {

			try {
				assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));
			} catch (SQLException e1) {
				logUnexpectedException(e1);
			}
		}
	}

	@Test
	public void testGetAllTokens_OK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, firstToken);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.createToken(dbConnection, secondToken);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));

			List<Token> tokens = tokenDAO.getAllTokens(dbConnection);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));
			assertEquals(tokenDAO.countTokensTableRows(dbConnection), tokens.size());

		} catch (SQLException e) {

			logUnexpectedException(e);
		}
	}

	@Test
	public void testGetAllTokens_NOK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			List<Token> tokens = tokenDAO.getAllTokens(dbConnection);
			assertEquals(tokenDAO.countTokensTableRows(dbConnection), tokens.size());

		} catch (SQLException e) {

			logUnexpectedException(e);
		}
	}

	@Test
	public void testUpdateTokenByTokenValue_OK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, firstToken);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			List<Token> tokens = tokenDAO.getAllTokens(dbConnection);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));
			assertEquals(tokenDAO.countTokensTableRows(dbConnection), tokens.size());

			Token createdToken = tokens.get(0);
			assertEquals(0, firstToken.compareTo(createdToken));

			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.updateTokenByTokenValue(dbConnection, createdToken.getTokenValue(), secondToken);

			List<Token> updatedTokenList = tokenDAO.getAllTokens(dbConnection);
			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));
			assertEquals(tokenDAO.countTokensTableRows(dbConnection), updatedTokenList.size());

			Token updatedToken = updatedTokenList.get(0);
			assertEquals(0, secondToken.compareTo(updatedToken));

		} catch (SQLException e) {

			logUnexpectedException(e);
		}
	}

	@Test
	public void testUpdateTokenByTokenValue_NOK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, firstToken);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			List<Token> tokens = tokenDAO.getAllTokens(dbConnection);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));
			assertEquals(tokenDAO.countTokensTableRows(dbConnection), tokens.size());

			Token createdToken = tokens.get(0);
			assertEquals(0, firstToken.compareTo(createdToken));

			String unknownTokenValue = "unknownTokenValue";
			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.updateTokenByTokenValue(dbConnection, unknownTokenValue, secondToken);

			List<Token> updatedTokenList = tokenDAO.getAllTokens(dbConnection);
			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));
			assertEquals(tokenDAO.countTokensTableRows(dbConnection), updatedTokenList.size());

			// The token was not updated because the given token value was not into the db.
			Token updatedToken = updatedTokenList.get(0);
			assertEquals(0, firstToken.compareTo(updatedToken));

		} catch (SQLException e) {

			logUnexpectedException(e);
		}
	}

	@Test
	public void testDeleteTokenByTokenValue_OK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, firstToken);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.createToken(dbConnection, secondToken);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));

			List<Token> tokens = tokenDAO.getAllTokens(dbConnection);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));
			assertEquals(tokenDAO.countTokensTableRows(dbConnection), tokens.size());

			tokenDAO.deleteTokenByTokenValue(dbConnection, firstToken.getTokenValue());
			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			tokenDAO.deleteTokenByTokenValue(dbConnection, secondToken.getTokenValue());

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

		} catch (SQLException e) {

			logUnexpectedException(e);
		}
	}

	@Test
	public void testDeleteTokenByTokenValue_NOK() {

		try {

			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, firstToken);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.createToken(dbConnection, secondToken);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));

			List<Token> tokens = tokenDAO.getAllTokens(dbConnection);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));
			assertEquals(tokenDAO.countTokensTableRows(dbConnection), tokens.size());

			String unknownTokenValue = "unknownTokenValue";
			tokenDAO.deleteTokenByTokenValue(dbConnection, unknownTokenValue);
			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));

		} catch (SQLException e) {

			logUnexpectedException(e);
		}
	}

	@Test
	public void testDeleteTokenByUsername_OK() {

		try {
			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, firstToken);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.createToken(dbConnection, secondToken);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));

			List<Token> tokens = tokenDAO.getAllTokens(dbConnection);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));
			assertEquals(tokenDAO.countTokensTableRows(dbConnection), tokens.size());

			tokenDAO.deleteTokenByUsername(dbConnection, firstToken.getUsername());
			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			tokenDAO.deleteTokenByUsername(dbConnection, secondToken.getUsername());
			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));
		} catch (SQLException e) {

			logUnexpectedException(e);
		}
	}

	@Test
	public void testDeleteTokenByUsername_NOK() {

		try {
			assertEquals(0, tokenDAO.countTokensTableRows(dbConnection));

			Token firstToken = TokenFactoryForTests.getDefaultTestToken();
			tokenDAO.createToken(dbConnection, firstToken);

			assertEquals(1, tokenDAO.countTokensTableRows(dbConnection));

			Token secondToken = TokenFactoryForTests.getDefaultTestToken(2);
			tokenDAO.createToken(dbConnection, secondToken);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));

			List<Token> tokens = tokenDAO.getAllTokens(dbConnection);

			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));
			assertEquals(tokenDAO.countTokensTableRows(dbConnection), tokens.size());

			String unknownUsername = "unknownUsername";
			tokenDAO.deleteTokenByUsername(dbConnection, unknownUsername);
			assertEquals(2, tokenDAO.countTokensTableRows(dbConnection));

		} catch (SQLException e) {
			
			logUnexpectedException(e);
		}
	}
}