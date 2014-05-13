package gp.e3.autheo.authentication.persistence.daos;

import static org.junit.Assert.*;
import gp.e3.autheo.authentication.domain.entities.ApiUser;
import gp.e3.autheo.util.ApiUserFactoryForTests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class ApiUserDAOTest {

	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static DBI dbi;
	private static Handle handle;
	private static IApiUserDAO apiUserDAO;

	@BeforeClass
	public static void setUpClass() {

		dbi = new DBI(H2_IN_MEMORY_DB);
		handle = dbi.open();
		apiUserDAO = handle.attach(IApiUserDAO.class);
	}

	@AfterClass
	public static void tearDownClass() {

		dbi.close(apiUserDAO);
		apiUserDAO = null;
		handle.close();
		dbi = null;
	}

	@Before
	public void setUp() {

		apiUserDAO.createApiUsersTableIfNotExists();
	}

	@After
	public void tearDown() {

		handle.execute("DROP TABLE api_users");
	}

	@Test
	public void testCountApiUsersTable_OK() {

		try {
			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);

			apiUserDAO.createApiUser(apiUser1.getName(), apiUser1.getUsername(), apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());

			assertEquals(1, apiUserDAO.countApiUsersTable());
			assertEquals(apiUserDAO.getAllApiUsers().size(), apiUserDAO.countApiUsersTable());

		} catch (Exception e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testCountApiUsersTable_NOK() {

		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			assertEquals(apiUserDAO.getAllApiUsers().size(), apiUserDAO.countApiUsersTable());

		} catch (Exception e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testCreateApiUser_OK() {

		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			apiUserDAO.createApiUser(apiUser1.getName(), apiUser1.getUsername(), apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());

			assertEquals(1, apiUserDAO.countApiUsersTable());

			ApiUser apiUser2 = ApiUserFactoryForTests.getDefaultTestApiUser(2);
			apiUserDAO.createApiUser(apiUser2.getName(), apiUser2.getUsername(), apiUser2.getPassword(), "salt", apiUser2.getOrganizationId(), apiUser2.getRoleId(), 
					apiUser2.getTokenValue());

			assertEquals(2, apiUserDAO.countApiUsersTable());

		} catch (Exception e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testCreateApiUser_NOK() {

		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			apiUserDAO.createApiUser(apiUser1.getName(), apiUser1.getUsername(), apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());

			assertEquals(1, apiUserDAO.countApiUsersTable());

			ApiUser duplicatedUser = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			apiUserDAO.createApiUser(duplicatedUser.getName(), duplicatedUser.getUsername(), duplicatedUser.getPassword(), 
					"salt", duplicatedUser.getOrganizationId(), duplicatedUser.getRoleId(), duplicatedUser.getTokenValue());

			fail("The method should threw an exception because the user with username " + duplicatedUser.getUsername() + " already exists into the db.");

		} catch (Exception e) {

			assertTrue(true);
		}
	}

	@Test
	public void testGetApiUserByUsername_OK() {

		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			apiUserDAO.createApiUser(apiUser1.getName(), apiUser1.getUsername(), apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());

			assertEquals(1, apiUserDAO.countApiUsersTable());
			
			ApiUser retrievedApiUser = apiUserDAO.getApiUserByUsername(apiUser1.getUsername());
			
			assertNotNull(retrievedApiUser);
			assertEquals(0, apiUser1.compareTo(retrievedApiUser));
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetApiUserByUsername_NOK() {

		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			ApiUser retrievedApiUser = apiUserDAO.getApiUserByUsername(apiUser1.getUsername());
			
			assertNull(retrievedApiUser);
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetApiUserByToken_OK() {
		
		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			apiUserDAO.createApiUser(apiUser1.getName(), apiUser1.getUsername(), apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());

			assertEquals(1, apiUserDAO.countApiUsersTable());
			
			ApiUser retrievedApiUser = apiUserDAO.getApiUserByToken(apiUser1.getTokenValue());
			
			assertNotNull(retrievedApiUser);
			assertEquals(0, apiUser1.compareTo(retrievedApiUser));
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetApiUserByToken_NOK() {
		
		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			ApiUser retrievedApiUser = apiUserDAO.getApiUserByToken(apiUser1.getTokenValue());
			
			assertNull(retrievedApiUser);
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetAllApiUsers_OK() {
		
		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			apiUserDAO.createApiUser(apiUser1.getName(), apiUser1.getUsername(), apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());

			assertEquals(1, apiUserDAO.countApiUsersTable());
			
			ApiUser apiUser2 = ApiUserFactoryForTests.getDefaultTestApiUser(2);
			apiUserDAO.createApiUser(apiUser2.getName(), apiUser2.getUsername(), apiUser2.getPassword(), "salt", apiUser2.getOrganizationId(), apiUser2.getRoleId(), 
					apiUser2.getTokenValue());
			
			assertEquals(2, apiUserDAO.countApiUsersTable());
			assertEquals(2, apiUserDAO.getAllApiUsers().size());
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetAllApiUsers_NOK() {
		
		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			assertEquals(0, apiUserDAO.getAllApiUsers().size());
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetPasswordByUsername_OK() {
		
		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			apiUserDAO.createApiUser(apiUser1.getName(), apiUser1.getUsername(), apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());

			assertEquals(1, apiUserDAO.countApiUsersTable());
			
			String password = apiUserDAO.getPasswordByUsername(apiUser1.getUsername());
			assertEquals(apiUser1.getPassword(), password);
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetPasswordByUsername_NOK() {
		
		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			String unknownPassword = "unknownPassword";
			String password = apiUserDAO.getPasswordByUsername(unknownPassword);
			
			assertNull(password);
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testUpdateApiUser_OK() {
		
		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			apiUserDAO.createApiUser(apiUser1.getName(), apiUser1.getUsername(), apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());

			assertEquals(1, apiUserDAO.countApiUsersTable());
			
			ApiUser apiUser2 = ApiUserFactoryForTests.getDefaultTestApiUser(2);
			apiUserDAO.updateApiUser(apiUser1.getUsername(), apiUser2.getName(), apiUser2.getPassword(), "updatedSalt", 
					apiUser2.getRoleId(), apiUser2.getTokenValue());
			
			ApiUser updatedApiUserFromDb = apiUserDAO.getApiUserByUsername(apiUser1.getUsername());
			
			assertNotNull(updatedApiUserFromDb);
			assertEquals(1, apiUserDAO.countApiUsersTable());
			assertEquals(apiUser1.getUsername(), updatedApiUserFromDb.getUsername());
			assertEquals(apiUser1.getOrganizationId(), updatedApiUserFromDb.getOrganizationId());
			
			assertEquals(apiUser2.getName(), updatedApiUserFromDb.getName());
			assertEquals(apiUser2.getPassword(), updatedApiUserFromDb.getPassword());
			assertEquals(apiUser2.getRoleId(), updatedApiUserFromDb.getRoleId());
			assertEquals(apiUser2.getTokenValue(), updatedApiUserFromDb.getTokenValue());
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testUpdateApiUser_NOK() {
		
		try {

			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			apiUserDAO.createApiUser(apiUser1.getName(), apiUser1.getUsername(), apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());

			assertEquals(1, apiUserDAO.countApiUsersTable());
			
			ApiUser apiUser2 = ApiUserFactoryForTests.getDefaultTestApiUser(2);
			// Will not update because the is not an api user with username apiUser2.getUsername() into the db.
			apiUserDAO.updateApiUser(apiUser2.getUsername(), apiUser2.getName(), apiUser2.getPassword(), "updatedSalt", 
					apiUser2.getRoleId(), apiUser2.getTokenValue());
			
			
			ApiUser notExistentApiUser = apiUserDAO.getApiUserByUsername(apiUser2.getUsername());
			assertNull(notExistentApiUser);
			
			ApiUser notUpdatedApiUser = apiUserDAO.getApiUserByUsername(apiUser1.getUsername());
			assertNotNull(notUpdatedApiUser);
			assertEquals(0, apiUser1.compareTo(notUpdatedApiUser));
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testDeleteApiUser_OK() {
		
		try {
			
			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			
			String username = apiUser1.getUsername();
			apiUserDAO.createApiUser(apiUser1.getName(), username, apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());
			
			assertEquals(1, apiUserDAO.countApiUsersTable());
			apiUserDAO.deleteApiUser(username);
			assertEquals(0, apiUserDAO.countApiUsersTable());
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testDeleteApiUser_NOK() {
		
		try {
			
			assertEquals(0, apiUserDAO.countApiUsersTable());
			ApiUser apiUser1 = ApiUserFactoryForTests.getDefaultTestApiUser(1);
			
			String username = apiUser1.getUsername();
			apiUserDAO.createApiUser(apiUser1.getName(), username, apiUser1.getPassword(), "salt", apiUser1.getOrganizationId(), apiUser1.getRoleId(), 
					apiUser1.getTokenValue());
			
			String unknownUsername = "unknownUsername";
			
			assertEquals(1, apiUserDAO.countApiUsersTable());
			apiUserDAO.deleteApiUser(unknownUsername);
			assertEquals(1, apiUserDAO.countApiUsersTable());
			apiUserDAO.deleteApiUser(username);
			assertEquals(0, apiUserDAO.countApiUsersTable());
			
			apiUserDAO.deleteApiUser(unknownUsername);
			assertEquals(0, apiUserDAO.countApiUsersTable());
			
		} catch (Exception e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}