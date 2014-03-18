package gp.e3.autheo.authorization.persistence.daos;

import static org.junit.Assert.*;

import java.util.List;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.util.RoleFactoryForTests;
import gp.e3.autheo.util.UserFactoryForTests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class RoleDAOTest {

	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static DBI dbi;
	private static Handle handle;

	private static IRoleDAO roleDAO;

	@BeforeClass
	public static void setUpClass() {

		dbi = new DBI(H2_IN_MEMORY_DB);
		handle = dbi.open();

		roleDAO = handle.attach(IRoleDAO.class);
	}

	@AfterClass
	public static void tearDownClass() {

		handle.close();
		dbi.close(roleDAO);

		roleDAO = null;

		dbi = null;
	}

	@Before
	public void setUp() {

		roleDAO.createRolesTable();
		roleDAO.createRolesAndUsersTable();
		roleDAO.createRolesAndPermissionsTable();
	}

	@After
	public void tearDown() {

		handle.execute("DROP TABLE roles_permissions");
		handle.execute("DROP TABLE roles_users");
		handle.execute("DROP TABLE roles");
	}

	@Test
	public void testCreateRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRolesTable());

		try {
			roleDAO.createRole(role.getName());
		} catch (Exception e) {
			fail("The method sould not throw an exception.");
		}

		assertEquals(1, roleDAO.countRolesTable());

		List<String> rolesNames = roleDAO.getAllRolesNames();

		assertEquals(1, rolesNames.size());
		assertEquals(role.getName(), rolesNames.get(0));
	}

	@Test
	public void testCreateRole_NOK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRolesTable());

		try {
			roleDAO.createRole(role.getName());
		} catch (Exception e) {
			fail("The method sould not throw an exception.");
		}

		assertEquals(1, roleDAO.countRolesTable());

		List<String> rolesNames = roleDAO.getAllRolesNames();

		assertEquals(1, rolesNames.size());
		assertEquals(role.getName(), rolesNames.get(0));

		try {

			roleDAO.createRole(role.getName());
			fail("The method should throw an exception because the given role name is already into the db.");

		} catch (Exception e) {

			assertNotNull(e);
		}
	}

	@Test
	public void testGetAllRolesNames_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRolesTable());

		try {
			roleDAO.createRole(role.getName());
		} catch (Exception e) {
			fail("The method sould not throw an exception.");
		}

		assertEquals(1, roleDAO.countRolesTable());

		List<String> rolesNames = roleDAO.getAllRolesNames();

		assertEquals(1, rolesNames.size());
		assertEquals(role.getName(), rolesNames.get(0));
		
		Role secondRole = RoleFactoryForTests.getDefaultTestRole(2);
		
		try {
			roleDAO.createRole(secondRole.getName());
		} catch (Exception e) {
			fail("The method sould not throw an exception.");
		}
		
		assertEquals(2, roleDAO.countRolesTable());

		rolesNames = roleDAO.getAllRolesNames();
		assertEquals(2, rolesNames.size());
	}
	
	@Test
	public void testGetAllRolesNames_NOK() {


		List<String> rolesNames = roleDAO.getAllRolesNames();
		assertEquals(0, rolesNames.size());
	}
	
	@Test
	public void testDeleteRole_OK() {
		
		Role defaultRole = RoleFactoryForTests.getDefaultTestRole();
		Role secondRole = RoleFactoryForTests.getDefaultTestRole(2);

		assertEquals(0, roleDAO.countRolesTable());

		try {
			roleDAO.createRole(defaultRole.getName());
			roleDAO.createRole(secondRole.getName());
		} catch (Exception e) {
			fail("The method sould not throw an exception.");
		}

		assertEquals(2, roleDAO.countRolesTable());

		roleDAO.deleteRole(defaultRole.getName());
		
		assertEquals(1, roleDAO.countRolesTable());
		
		List<String> allRolesNames = roleDAO.getAllRolesNames();
		assertEquals(1, allRolesNames.size());
		assertEquals(secondRole.getName(), allRolesNames.get(0));
		
		roleDAO.deleteRole(secondRole.getName());
		
		assertEquals(0, roleDAO.countRolesTable());
	}
	
	@Test
	public void testDeleteRole_NOK() {
		
		String fakeRoleName = "fakeRoleName";
		
		assertEquals(0, roleDAO.countRolesTable());
		roleDAO.deleteRole(fakeRoleName);
		assertEquals(0, roleDAO.countRolesTable());
	}
	
	@Test
	public void testAddUserToRole_OK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		Role role = RoleFactoryForTests.getDefaultTestRole();
		
		assertEquals(0, roleDAO.countRoleUsersTable());
		roleDAO.addUserToRole(user.getUsername(), role.getName());
		assertEquals(1, roleDAO.countRoleUsersTable());
		
		String secondUsername = "second";
		String thirdUsername = "third";
		
		roleDAO.addUserToRole(secondUsername, role.getName());
		roleDAO.addUserToRole(thirdUsername, role.getName());
		
		assertEquals(3, roleDAO.countRoleUsersTable());
	}
	
	@Test
	public void testAddUserToRole_NOK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		Role role = RoleFactoryForTests.getDefaultTestRole();
		
		assertEquals(0, roleDAO.countRoleUsersTable());
		roleDAO.addUserToRole(user.getUsername(), role.getName());
		assertEquals(1, roleDAO.countRoleUsersTable());
		
		try {
			
			roleDAO.addUserToRole(user.getUsername(), role.getName());
			fail("The method should thrown an exception because the username is already been assigned a role.");
			
		} catch (Exception e) {
			
			assertNotNull(e);
		}
	}
	
	@Test
	public void testRemoveUserFromRole_OK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		Role role = RoleFactoryForTests.getDefaultTestRole();
		
		assertEquals(0, roleDAO.countRoleUsersTable());
		roleDAO.addUserToRole(user.getUsername(), role.getName());
		assertEquals(1, roleDAO.countRoleUsersTable());
		
		String secondUsername = "second";
		String thirdUsername = "third";
		
		roleDAO.addUserToRole(secondUsername, role.getName());
		roleDAO.addUserToRole(thirdUsername, role.getName());
		
		assertEquals(3, roleDAO.countRoleUsersTable());
		
		roleDAO.removeUserFromRole(secondUsername);
		assertEquals(2, roleDAO.countRoleUsersTable());
		
		// The same username so nothing changes.
		roleDAO.removeUserFromRole(secondUsername);
		assertEquals(2, roleDAO.countRoleUsersTable());
		
		roleDAO.removeUserFromRole(user.getUsername());
		assertEquals(1, roleDAO.countRoleUsersTable());
		
		roleDAO.removeUserFromRole(thirdUsername);
		assertEquals(0, roleDAO.countRoleUsersTable());
	}
	
	@Test
	public void testRemoveUserFromRole_NOK() {
		
		String fakeUsername = "fakeUsername";
		
		assertEquals(0, roleDAO.countRoleUsersTable());
		roleDAO.removeUserFromRole(fakeUsername);
		assertEquals(0, roleDAO.countRoleUsersTable());
	}
	
	@Test
	public void testRemoveAllUsersFromRole_OK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		Role role = RoleFactoryForTests.getDefaultTestRole();
		
		assertEquals(0, roleDAO.countRoleUsersTable());
		roleDAO.addUserToRole(user.getUsername(), role.getName());
		assertEquals(1, roleDAO.countRoleUsersTable());
		
		String secondUsername = "second";
		String thirdUsername = "third";
		
		roleDAO.addUserToRole(secondUsername, role.getName());
		roleDAO.addUserToRole(thirdUsername, role.getName());
		
		assertEquals(3, roleDAO.countRoleUsersTable());
		
		roleDAO.removeAllUsersFromRole(role.getName());
		
		assertEquals(0, roleDAO.countRoleUsersTable());
	}
	
	@Test
	public void testRemoveAllUsersFromRole_NOK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		Role role = RoleFactoryForTests.getDefaultTestRole();
		
		assertEquals(0, roleDAO.countRoleUsersTable());
		roleDAO.addUserToRole(user.getUsername(), role.getName());
		assertEquals(1, roleDAO.countRoleUsersTable());
		
		String secondUsername = "second";
		String thirdUsername = "third";
		
		roleDAO.addUserToRole(secondUsername, role.getName());
		roleDAO.addUserToRole(thirdUsername, role.getName());
		
		assertEquals(3, roleDAO.countRoleUsersTable());
		
		String fakeRoleName = "fakeRoleName";
		roleDAO.removeAllUsersFromRole(fakeRoleName);
		
		assertEquals(3, roleDAO.countRoleUsersTable());
	}
}