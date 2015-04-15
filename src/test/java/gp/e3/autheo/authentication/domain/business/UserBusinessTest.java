package gp.e3.autheo.authentication.domain.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.persistence.daos.UserDAO;
import gp.e3.autheo.util.ExceptionUtilsForTests;
import gp.e3.autheo.util.UserFactoryForTests;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UserBusinessTest {

	private UserDAO userDaoMock;
	private Connection dbConnectionMock;
	private BasicDataSource dataSourceMock;

	@Before
	public void setUp() {

		userDaoMock = Mockito.mock(UserDAO.class);
		dbConnectionMock = Mockito.mock(Connection.class);
		dataSourceMock = Mockito.mock(BasicDataSource.class);

		try {
			Mockito.when(dataSourceMock.getConnection()).thenReturn(dbConnectionMock);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {

		userDaoMock = null;
		dbConnectionMock = null;
		dataSourceMock = null;
	}

	@Test
	public void testCreateUser_OK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();

			User expectedUser = user;
			Mockito.when(userDaoMock.createUser(Mockito.any(Connection.class), Mockito.any(User.class), Mockito.anyString(), Mockito.anyString())).thenReturn(expectedUser);
			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			
			User createdUser = userBusiness.createUser(user);
			assertEquals(0, expectedUser.compareTo(createdUser));
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testCreateUser_NOK() {

		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			
			Mockito.doThrow(SQLException.class).when(userDaoMock).createUser(Mockito.any(Connection.class), Mockito.any(User.class), Mockito.anyString(), Mockito.anyString());
			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			userBusiness.createUser(user);
			
			fail("The method should throw an exception");
			
		} catch (SQLException | IllegalStateException e) {
			
			assertNotNull(e);
		}
	}

	@Test
	public void testAuthenticateUser_OK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String originalPassword = user.getPassword();
			String passwordHash = PasswordHandler.getPasswordHash(originalPassword);
			Mockito.when(userDaoMock.getPasswordByUsername(dbConnectionMock, user.getUsername())).thenReturn(passwordHash);

			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			assertEquals(true, userBusiness.authenticateUser(user.getUsername(), user.getPassword()));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException e) {

			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testAuthenticateUser_NOK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String originalPassword = user.getPassword();
			String passwordHash = PasswordHandler.getPasswordHash(originalPassword);
			Mockito.when(userDaoMock.getPasswordByUsername(dbConnectionMock, user.getUsername())).thenReturn(passwordHash);

			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);

			// The password is modified in order to fail the authentication process.
			String modifiedOriginalPassword = user.getPassword() + "qwe123";

			assertEquals(false, userBusiness.authenticateUser(user.getUsername(), modifiedOriginalPassword));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException e) {

			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testGetUserByUsername_OK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			Mockito.when(userDaoMock.getUserByUsername(dbConnectionMock, user.getUsername())).thenReturn(user);

			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			User retrievedUser = userBusiness.getUserByUsername(user.getUsername());

			assertNotNull(retrievedUser);
			assertEquals(0, user.compareTo(retrievedUser));
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testGetUserByUsername_NOK() {

		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();
			Mockito.when(userDaoMock.getUserByUsername(dbConnectionMock, user.getUsername())).thenReturn(null);

			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			User retrievedUser = userBusiness.getUserByUsername(user.getUsername());
			assertNull(retrievedUser);
			
		} catch (SQLException e) {

			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testGetAllUsers_OK() {
		
		try {
			
			int listSize = 5;
			List<User> userList = UserFactoryForTests.getUserList(listSize);
			Mockito.when(userDaoMock.getAllUsers(dbConnectionMock)).thenReturn(userList);

			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			List<User> retreivedUserList = userBusiness.getAllUsers();
			assertEquals(listSize, retreivedUserList.size());
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testGetAllUsers_NOK() {
		
		try {
			
			int listSize = 0;
			List<User> userList = UserFactoryForTests.getUserList(listSize);
			Mockito.when(userDaoMock.getAllUsers(dbConnectionMock)).thenReturn(userList);

			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			List<User> retreivedUserList = userBusiness.getAllUsers();
			assertEquals(listSize, retreivedUserList.size());
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testUpdateUser_OK() {
		
		try {
			
			User defaultUser = UserFactoryForTests.getDefaultTestUser();
			User updatedUser = UserFactoryForTests.getDefaultTestUser(1);

			boolean expectedUserWasUpdated = true;
			Mockito.when(userDaoMock.updateUser(dbConnectionMock, defaultUser.getUsername(), updatedUser.getName(), 
					updatedUser.getPassword())).thenReturn(expectedUserWasUpdated);

			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			boolean userWasUpdated = userBusiness.updateUser(defaultUser.getUsername(), updatedUser);
			assertEquals(true, userWasUpdated);
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testUpdateUser_NOK() {
		
		try {
			
			// The default user was never used in this test.
			// User defaultUser = UserFactoryForTests.getDefaultTestUser();
			User updatedUser = UserFactoryForTests.getDefaultTestUser(1);

			boolean expectedUserWasUpdated = false;
			Mockito.when(userDaoMock.updateUser(dbConnectionMock, updatedUser.getUsername(), updatedUser.getName(), 
					updatedUser.getPassword())).thenReturn(expectedUserWasUpdated);

			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			boolean userWasUpdated = userBusiness.updateUser(updatedUser.getUsername(), updatedUser);
			assertEquals(false, userWasUpdated);
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testDeleteUser_OK() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();

			boolean expectedUserWasDeleted = true;
			Mockito.when(userDaoMock.deleteUser(dbConnectionMock, user.getUsername())).thenReturn(expectedUserWasDeleted);

			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			boolean userWasDeleted = userBusiness.deleteUser(user.getUsername());
			assertEquals(true, userWasDeleted);
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testDeleteUser_NOK() {
		
		try {
			
			boolean expectedUserWasDeleted = false;
			String unknownUsername = "unknownUsername";
			Mockito.when(userDaoMock.deleteUser(dbConnectionMock, unknownUsername)).thenReturn(expectedUserWasDeleted);

			UserBusiness userBusiness = new UserBusiness(dataSourceMock, userDaoMock);
			boolean userWasDeleted = userBusiness.deleteUser(unknownUsername);
			assertEquals(false, userWasDeleted);
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}
}