package gp.e3.autheo.persistence.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.persistence.daos.IUserDAO;
import gp.e3.autheo.util.UserFactoryForTests;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class UserDAOTest {

	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static DBI dbi;
	private static Handle handle;
	private static IUserDAO userDAO;

	@BeforeClass
	public static void setUpClass() {

		dbi = new DBI(H2_IN_MEMORY_DB);
		handle = dbi.open();

		userDAO = handle.attach(IUserDAO.class);
	}

	@AfterClass
	public static void tearDownClass() {

		handle.close();
		dbi.close(userDAO);
		userDAO = null;
		dbi = null;
	}

	@Before
	public void setUp() {

		userDAO.createUsersTableIfNotExists();
	}

	@After
	public void tearDown() {

		handle.execute("DROP TABLE users");
	}

	@Test
	public void testCountUsersTable_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";

		try {

			assertEquals(0, userDAO.countUsersTable());
			userDAO.createUser(user.getName(), user.getUsername(), user.getPassword(), salt);

			int numberOfUsersIntoDb = userDAO.getAllUsers().size();

			assertEquals(1, numberOfUsersIntoDb);
			assertEquals(numberOfUsersIntoDb, userDAO.countUsersTable());

			User retrievedUser = userDAO.getUserByUsername(user.getUsername());
			assertEquals(0, user.compareTo(retrievedUser));

		} catch (Exception e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testCountUsersTable_NOK() {

		User user = UserFactoryForTests.getDefaultTestUser();

		try {

			assertEquals(0, userDAO.countUsersTable());
			int numberOfUsersIntoDb = userDAO.getAllUsers().size();

			assertEquals(0, numberOfUsersIntoDb);
			assertEquals(numberOfUsersIntoDb, userDAO.countUsersTable());

			User retrievedUser = userDAO.getUserByUsername(user.getUsername());
			assertEquals(0, user.compareTo(retrievedUser));

		} catch (Exception e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateUser_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";

		try {

			assertEquals(0, userDAO.countUsersTable());
			userDAO.createUser(user.getName(), user.getUsername(), user.getPassword(), salt);
			assertEquals(1, userDAO.countUsersTable());

			User retrievedUser = userDAO.getUserByUsername(user.getUsername());
			assertEquals(0, user.compareTo(retrievedUser));

		} catch (Exception e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateUser_NOK() {

		User user = new User(null, null, null);
		String salt = "salt";

		try {

			assertEquals(0, userDAO.countUsersTable());
			userDAO.createUser(user.getName(), user.getUsername(), user.getPassword(), salt);
			fail("Should throw an exception in the line above.");

		} catch (Exception e) {

			// Should get into this block.
		}
	}

	@Test
	public void testGetUserByUsername_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";

		try {

			userDAO.createUser(user.getName(), user.getUsername(), user.getPassword(), salt);

			User retrievedUser = userDAO.getUserByUsername(user.getUsername());
			assertEquals(0, user.compareTo(retrievedUser));

		} catch (Exception e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUserByUsername_NOK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";

		try {

			userDAO.createUser(user.getName(), user.getUsername(), user.getPassword(), salt);

			String unknownUsername = "unknownUsername";

			User retrievedUser = userDAO.getUserByUsername(unknownUsername);
			assertNull(retrievedUser);

		} catch (Exception e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testGetAllUsers_OK() {

		int listSize = 5;
		List<User> userList = UserFactoryForTests.getUserList(listSize);
		String salt = "123";

		try {

			for (User user : userList) {

				userDAO.createUser(user.getName(), user.getUsername(), user.getPassword(), salt);
			}

		} catch (Exception e) {

			fail(e.getMessage());
		}

		assertEquals(listSize, userDAO.countUsersTable());

		List<User> retrievedUsers = userDAO.getAllUsers();
		assertEquals(listSize, retrievedUsers.size());
	}

	@Test
	public void testGetAllUsers_NOK() {

		int listSize = 0;
		List<User> userList = UserFactoryForTests.getUserList(listSize);
		String salt = "123";

		try {

			for (User user : userList) {

				userDAO.createUser(user.getName(), user.getUsername(), user.getPassword(), salt);
			}

		} catch (Exception e) {

			fail(e.getMessage());
		}

		assertEquals(listSize, userDAO.countUsersTable());

		List<User> retrievedUsers = userDAO.getAllUsers();
		assertEquals(listSize, retrievedUsers.size());
	}

	@Test
	public void testGetPasswordByUsername_OK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";

		try {

			assertEquals(0, userDAO.countUsersTable());
			userDAO.createUser(user.getName(), user.getUsername(), user.getPassword(), salt);
			assertEquals(1, userDAO.countUsersTable());

			String password = userDAO.getPasswordByUsername(user.getUsername());
			assertEquals(user.getPassword(), password);

		} catch (Exception e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetPasswordByUsername_NOK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();

		try {

			assertEquals(0, userDAO.countUsersTable());

			String password = userDAO.getPasswordByUsername(user.getUsername());
			assertNull(password);

		} catch (Exception e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testUpdateUser_OK() {
		
		User defaultUser = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";

		try {

			assertEquals(0, userDAO.countUsersTable());
			userDAO.createUser(defaultUser.getName(), defaultUser.getUsername(), defaultUser.getPassword(), salt);
			assertEquals(1, userDAO.countUsersTable());

			User updatedUser = UserFactoryForTests.getDefaultTestUser(1);
			
			int numberOfRowsModified = userDAO.updateUser(defaultUser.getUsername(), updatedUser.getName(), 
					updatedUser.getPassword());
			
			assertEquals(1, numberOfRowsModified);
			assertEquals(1, userDAO.countUsersTable());
			
			User retrievedUser = userDAO.getUserByUsername(defaultUser.getUsername());
			
			assertEquals(defaultUser.getUsername(), retrievedUser.getUsername());
			
			assertEquals(updatedUser.getName(), retrievedUser.getName());
			assertEquals(updatedUser.getPassword(), retrievedUser.getPassword());

		} catch (Exception e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testUpdateUser_NOK() {
		
		User defaultUser = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";

		try {

			assertEquals(0, userDAO.countUsersTable());
			userDAO.createUser(defaultUser.getName(), defaultUser.getUsername(), defaultUser.getPassword(), salt);
			assertEquals(1, userDAO.countUsersTable());

			User updatedUser = UserFactoryForTests.getDefaultTestUser(1);
			
			String unknownUsername = "unknownUsername";
			
			int numberOfRowsModified = userDAO.updateUser(unknownUsername, updatedUser.getName(), 
					updatedUser.getPassword());
			
			assertEquals(0, numberOfRowsModified);
			assertEquals(1, userDAO.countUsersTable());
			
			User unknownUser = userDAO.getUserByUsername(unknownUsername);
			assertNull(unknownUser);
			
			User retrievedDefaultUser = userDAO.getUserByUsername(defaultUser.getUsername());
			
			assertEquals(defaultUser.getName(), retrievedDefaultUser.getName());
			assertEquals(defaultUser.getUsername(), retrievedDefaultUser.getUsername());
			assertEquals(defaultUser.getPassword(), retrievedDefaultUser.getPassword());

		} catch (Exception e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDeleteUser_OK() {
		
		User defaultUser = UserFactoryForTests.getDefaultTestUser();
		String salt = "salt";

		try {

			assertEquals(0, userDAO.countUsersTable());
			userDAO.createUser(defaultUser.getName(), defaultUser.getUsername(), defaultUser.getPassword(), salt);
			assertEquals(1, userDAO.countUsersTable());
			
			int numberOfRowsModified = userDAO.deleteUser(defaultUser.getUsername());
			
			assertEquals(1, numberOfRowsModified);
			assertEquals(0, userDAO.countUsersTable());

		} catch (Exception e) {
			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDeleteUser_NOK() {
		
		User defaultUser = UserFactoryForTests.getDefaultTestUser();

		try {

			assertEquals(0, userDAO.countUsersTable());
			int numberOfRowsModified = userDAO.deleteUser(defaultUser.getUsername());
			
			assertEquals(0, numberOfRowsModified);
			assertEquals(0, userDAO.countUsersTable());

		} catch (Exception e) {
			
			fail(e.getMessage());
		}
	}
}