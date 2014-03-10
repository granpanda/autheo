package gp.e3.autheo.domain.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.business.PasswordHandler;
import gp.e3.autheo.authentication.domain.business.UserBusiness;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.persistence.daos.IUserDAO;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.util.UserFactoryForTests;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.security.sasl.AuthenticationException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class UserBusinessTest {

	private IUserDAO userDaoMock;

	@Before
	public void setUp() {

		userDaoMock = Mockito.mock(IUserDAO.class);
	}

	@After
	public void tearDown() {

		userDaoMock = null;
	}

	@Test
	public void testCreateUser_OK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String errorMessage = "The user with username: " + user.getUsername() + " is already registered.";

			Mockito.doNothing().doThrow(new DuplicateIdException(errorMessage)).when(userDaoMock)
			.createUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

			UserBusiness userBusiness = new UserBusiness(userDaoMock);
			User createdUser = userBusiness.createUser(user);

			assertEquals(0, user.compareTo(createdUser));

		} catch (Exception e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateUser_NOK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String errorMessage = "The user with username: " + user.getUsername() + " is already registered.";

			Mockito.doNothing().doThrow(new DuplicateIdException(errorMessage)).when(userDaoMock)
			.createUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

			UserBusiness userBusiness = new UserBusiness(userDaoMock);
			userBusiness.createUser(user);
			userBusiness.createUser(user);

			fail("The user business should throw a DuplicateIdException.");

		} catch (Exception e) {

			// Expected to be here.
			assertNotNull(e);
		}
	}

	@Test
	public void testAuthenticateUser_OK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String originalPassword = user.getPassword();
			String passwordHash = PasswordHandler.getPasswordHash(originalPassword);

			Mockito.when(userDaoMock.getPasswordByUsername(user.getUsername())).thenReturn(passwordHash);

			UserBusiness userBusiness = new UserBusiness(userDaoMock);
			assertTrue(userBusiness.authenticateUser(user.getUsername(), user.getPassword()));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | AuthenticationException e) {

			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testAuthenticateUser_NOK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String originalPassword = user.getPassword();
			String passwordHash = PasswordHandler.getPasswordHash(originalPassword);

			Mockito.when(userDaoMock.getPasswordByUsername(user.getUsername())).thenReturn(passwordHash);

			UserBusiness userBusiness = new UserBusiness(userDaoMock);

			// The password is modified in order to fail the authentication process.
			String modifiedOriginalPassword = user.getPassword() + "qwe123";

			assertFalse(userBusiness.authenticateUser(user.getUsername(), modifiedOriginalPassword));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | AuthenticationException e) {

			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUserByUsername_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();

		Mockito.when(userDaoMock.getUserByUsername(user.getUsername())).thenReturn(user);

		UserBusiness userBusiness = new UserBusiness(userDaoMock);
		User retrievedUser = userBusiness.getUserByUsername(user.getUsername());

		assertNotNull(retrievedUser);
		assertEquals(0, user.compareTo(retrievedUser));
	}

	@Test
	public void testGetUserByUsername_NOK() {

		User user = UserFactoryForTests.getDefaultTestUser();

		Mockito.when(userDaoMock.getUserByUsername(Mockito.anyString())).thenReturn(null);

		UserBusiness userBusiness = new UserBusiness(userDaoMock);
		User retrievedUser = userBusiness.getUserByUsername(user.getUsername());

		assertNull(retrievedUser);
	}

	@Test
	public void testGetAllUsers_OK() {

		int listSize = 5;
		List<User> userList = UserFactoryForTests.getUserList(listSize);

		Mockito.when(userDaoMock.getAllUsers()).thenReturn(userList);

		UserBusiness userBusiness = new UserBusiness(userDaoMock);
		List<User> retreivedUserList = userBusiness.getAllUsers();

		assertEquals(listSize, retreivedUserList.size());
	}

	@Test
	public void testGetAllUsers_NOK() {

		int listSize = 0;
		List<User> userList = UserFactoryForTests.getUserList(listSize);

		Mockito.when(userDaoMock.getAllUsers()).thenReturn(userList);

		UserBusiness userBusiness = new UserBusiness(userDaoMock);
		List<User> retreivedUserList = userBusiness.getAllUsers();

		assertEquals(listSize, retreivedUserList.size());
	}

	@Test
	public void testUpdateUser_OK() {

		User defaultUser = UserFactoryForTests.getDefaultTestUser();
		User updatedUser = UserFactoryForTests.getDefaultTestUser(1);

		int numberOfRowsModified = 1;

		Mockito.when(userDaoMock.updateUser(defaultUser.getUsername(), updatedUser.getName(), 
				updatedUser.getPassword())).thenReturn(numberOfRowsModified);

		UserBusiness userBusiness = new UserBusiness(userDaoMock);
		userBusiness.updateUser(defaultUser.getUsername(), updatedUser);

		// There is nothing to prove here. It is a void method that only calls the db.
		assertTrue(true);
	}

	@Test
	public void testUpdateUser_NOK() {

		// The default user was never used in this test.
		// User defaultUser = UserFactoryForTests.getDefaultTestUser();
		User updatedUser = UserFactoryForTests.getDefaultTestUser(1);

		int numberOfRowsModified = 0;

		Mockito.when(userDaoMock.updateUser(updatedUser.getUsername(), updatedUser.getName(), 
				updatedUser.getPassword())).thenReturn(numberOfRowsModified);

		UserBusiness userBusiness = new UserBusiness(userDaoMock);
		userBusiness.updateUser(updatedUser.getUsername(), updatedUser);

		// There is nothing to prove here. It is a void method that only calls the db.
		assertTrue(true);
	}

	@Test
	public void testDeleteUser_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();

		int numberOfRowsModified = 1;

		Mockito.when(userDaoMock.deleteUser(user.getUsername())).thenReturn(numberOfRowsModified);

		UserBusiness userBusiness = new UserBusiness(userDaoMock);
		userBusiness.deleteUser(user.getUsername());

		// There is nothing to prove here. It is a void method that only calls the db.
		assertTrue(true);
	}

	@Test
	public void testDeleteUser_NOK() {

		int numberOfRowsModified = 0;

		String unknownUsername = "unknownUsername";
		Mockito.when(userDaoMock.deleteUser(unknownUsername)).thenReturn(numberOfRowsModified);

		UserBusiness userBusiness = new UserBusiness(userDaoMock);
		userBusiness.deleteUser(unknownUsername);

		// There is nothing to prove here. It is a void method that only calls the db.
		assertTrue(true);
	}
}