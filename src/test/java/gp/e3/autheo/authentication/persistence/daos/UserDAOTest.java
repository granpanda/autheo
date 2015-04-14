package gp.e3.autheo.authentication.persistence.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.util.ExceptionUtilsForTests;
import gp.e3.autheo.util.UserFactoryForTests;

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

public class UserDAOTest {

	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static Connection dbConnection;
	private static UserDAO userDAO;

	@BeforeClass
	public static void setUpClass() {

		try {
			
			Class.forName("org.h2.Driver");
			dbConnection = DriverManager.getConnection(H2_IN_MEMORY_DB);
			userDAO = new UserDAO();
			
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownClass() {

		userDAO = null;
	}

	@Before
	public void setUp() {

		userDAO.createUsersTableIfNotExists(dbConnection);
	}

	@After
	public void tearDown() {
		
		try {
			
			String dropTableSQL = "DROP TABLE users";
			PreparedStatement prepareStatement = dbConnection.prepareStatement(dropTableSQL);
			prepareStatement.executeUpdate();
			prepareStatement.close();
			prepareStatement = null;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	@Test
	public void testCountUsersTable_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";
		
		try {
			
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			userDAO.createUser(dbConnection, user, user.getPassword(), salt);

			int numberOfUsersIntoDb = userDAO.getAllUsers(dbConnection).size();

			assertEquals(1, numberOfUsersIntoDb);
			assertEquals(numberOfUsersIntoDb, userDAO.countUsersTable(dbConnection));

			User retrievedUser = userDAO.getUserByUsername(dbConnection, user.getUsername());
			assertEquals(0, user.compareTo(retrievedUser));
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testCountUsersTable_NOK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		
		try {
			
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			int numberOfUsersIntoDb = userDAO.getAllUsers(dbConnection).size();

			assertEquals(0, numberOfUsersIntoDb);
			assertEquals(numberOfUsersIntoDb, userDAO.countUsersTable(dbConnection));

			User retrievedUser = userDAO.getUserByUsername(dbConnection, user.getUsername());
			assertEquals(0, user.compareTo(retrievedUser));
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testCreateUser_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";
		
		try {
			
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			User createdUser = userDAO.createUser(dbConnection, user, user.getPassword(), salt);
			assertNotNull(createdUser);

			assertEquals(1, userDAO.countUsersTable(dbConnection));
			User retrievedUser = userDAO.getUserByUsername(dbConnection, user.getUsername());
			assertEquals(0, user.compareTo(retrievedUser));
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testCreateUser_NOK_emptyUser() {

		User user = new User(null, null, null, false, null, null);
		String salt = "salt";
		
		try {
			
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			userDAO.createUser(dbConnection, user, user.getPassword(), salt);
			
			fail("The method shoud threw an exception");
			
		} catch (SQLException e) {
			
			assertNotNull(e);
		}
	}

	@Test
	public void testGetUserByUsername_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";
		
		try {
			
			userDAO.createUser(dbConnection, user, user.getPassword(), salt);
			User retrievedUser = userDAO.getUserByUsername(dbConnection, user.getUsername());
			assertEquals(0, user.compareTo(retrievedUser));
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testGetUserByUsername_NOK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";
		
		try {
			
			userDAO.createUser(dbConnection, user, user.getPassword(), salt);
			String unknownUsername = "unknownUsername";

			User retrievedUser = userDAO.getUserByUsername(dbConnection, unknownUsername);
			assertNull(retrievedUser);
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testGetAllUsers_OK() {

		int listSize = 5;
		List<User> userList = UserFactoryForTests.getUserList(listSize);
		String salt = "123";
		
		try {
			
			for (User user : userList) {
				userDAO.createUser(dbConnection, user, user.getPassword(), salt);
			}

			assertEquals(listSize, userDAO.countUsersTable(dbConnection));
			List<User> retrievedUsers = userDAO.getAllUsers(dbConnection);
			assertEquals(listSize, retrievedUsers.size());
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testGetAllUsers_NOK() {

		int listSize = 0;
		List<User> userList = UserFactoryForTests.getUserList(listSize);
		String salt = "123";
		
		try {
			
			for (User user : userList) {
				userDAO.createUser(dbConnection, user, user.getPassword(), salt);
			}

			assertEquals(listSize, userDAO.countUsersTable(dbConnection));
			List<User> retrievedUsers = userDAO.getAllUsers(dbConnection);
			assertEquals(listSize, retrievedUsers.size());
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testGetPasswordByUsername_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";
		
		try {
			
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			userDAO.createUser(dbConnection, user, user.getPassword(), salt);
			assertEquals(1, userDAO.countUsersTable(dbConnection));

			String password = userDAO.getPasswordByUsername(dbConnection, user.getUsername());
			assertEquals(user.getPassword(), password);
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testGetPasswordByUsername_NOK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		
		try {
			
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			String password = userDAO.getPasswordByUsername(dbConnection, user.getUsername());
			
			assertNotNull(password);
			assertEquals("", password);
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
		
	}

	@Test
	public void testUpdateUser_OK() {

		User defaultUser = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";
		
		try {
			
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			userDAO.createUser(dbConnection, defaultUser, defaultUser.getPassword(), salt);
			assertEquals(1, userDAO.countUsersTable(dbConnection));

			User updatedUser = UserFactoryForTests.getDefaultTestUser(1);
			boolean userWasUpdated = userDAO.updateUser(dbConnection, defaultUser.getUsername(), updatedUser.getName(), updatedUser.getPassword());

			assertEquals(true, userWasUpdated);
			assertEquals(1, userDAO.countUsersTable(dbConnection));

			User retrievedUser = userDAO.getUserByUsername(dbConnection, defaultUser.getUsername());
			assertEquals(defaultUser.getUsername(), retrievedUser.getUsername());
			assertEquals(updatedUser.getName(), retrievedUser.getName());
			assertEquals(updatedUser.getPassword(), retrievedUser.getPassword());
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testUpdateUser_NOK() {

		User defaultUser = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";
		
		try {
			
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			userDAO.createUser(dbConnection, defaultUser, defaultUser.getPassword(), salt);
			assertEquals(1, userDAO.countUsersTable(dbConnection));

			User updatedUser = UserFactoryForTests.getDefaultTestUser(1);

			String unknownUsername = "unknownUsername";
			boolean userWasUpdated = userDAO.updateUser(dbConnection, unknownUsername, updatedUser.getName(), updatedUser.getPassword());

			assertEquals(false, userWasUpdated);
			assertEquals(1, userDAO.countUsersTable(dbConnection));

			User unknownUser = userDAO.getUserByUsername(dbConnection, unknownUsername);
			assertNull(unknownUser);

			User retrievedDefaultUser = userDAO.getUserByUsername(dbConnection, defaultUser.getUsername());
			assertEquals(defaultUser.getName(), retrievedDefaultUser.getName());
			assertEquals(defaultUser.getUsername(), retrievedDefaultUser.getUsername());
			assertEquals(defaultUser.getPassword(), retrievedDefaultUser.getPassword());
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testDeleteUser_OK() {

		User defaultUser = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";
		
		try {
			
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			userDAO.createUser(dbConnection, defaultUser, defaultUser.getPassword(), salt);
			assertEquals(1, userDAO.countUsersTable(dbConnection));

			boolean userWasDeleted = userDAO.deleteUser(dbConnection, defaultUser.getUsername());
			assertEquals(true, userWasDeleted);
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testDeleteUser_NOK() {

		User defaultUser = UserFactoryForTests.getDefaultTestUser();
		
		try {
			
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			boolean userWasDeleted = userDAO.deleteUser(dbConnection, defaultUser.getUsername());

			assertEquals(false, userWasDeleted);
			assertEquals(0, userDAO.countUsersTable(dbConnection));
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}
}