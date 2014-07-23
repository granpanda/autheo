package gp.e3.autheo.authorization.persistence.daos;

import static org.junit.Assert.assertEquals;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.infrastructure.utils.SqlUtils;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.util.RoleFactoryForTests;
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

public class RoleDAOTest {

	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static Connection dbConnection;
	private static RoleDAO roleDAO;

	@BeforeClass
	public static void setUpClass() {

		try {

			Class.forName("org.h2.Driver");
			dbConnection = DriverManager.getConnection(H2_IN_MEMORY_DB);

			roleDAO = new RoleDAO();

		} catch (ClassNotFoundException | SQLException e) {

			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownClass() {

		roleDAO = null;
		SqlUtils.closeDbConnection(dbConnection);
	}

	@Before
	public void setUp() {

		roleDAO.createRolesTable(dbConnection);
		roleDAO.createRolesAndUsersTable(dbConnection);
		roleDAO.createRolesAndPermissionsTable(dbConnection);
	}

	@After
	public void tearDown() {

		try {

			String dropRolesPermissionsTable = "DROP TABLE roles_permissions;";
			PreparedStatement prepareStatement = dbConnection.prepareStatement(dropRolesPermissionsTable);
			prepareStatement.executeUpdate();
			prepareStatement.close();

			String dropRolesUsersTable = "DROP TABLE roles_users;";
			prepareStatement = dbConnection.prepareStatement(dropRolesUsersTable);
			prepareStatement.executeUpdate();
			prepareStatement.close();

			String dropRolesTable = "DROP TABLE roles;";
			prepareStatement = dbConnection.prepareStatement(dropRolesTable);
			prepareStatement.executeUpdate();
			prepareStatement.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	@Test
	public void testCreateRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRolesTable(dbConnection));
		roleDAO.createRole(dbConnection, role.getName());
		assertEquals(1, roleDAO.countRolesTable(dbConnection));

		List<String> rolesNames = roleDAO.getAllRolesNames(dbConnection);

		assertEquals(1, rolesNames.size());
		assertEquals(role.getName(), rolesNames.get(0));
	}

	@Test
	public void testCreateRole_NOK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRolesTable(dbConnection));
		roleDAO.createRole(dbConnection, role.getName());
		assertEquals(1, roleDAO.countRolesTable(dbConnection));

		List<String> rolesNames = roleDAO.getAllRolesNames(dbConnection);

		assertEquals(1, rolesNames.size());
		assertEquals(role.getName(), rolesNames.get(0));

		// The answer should be 0 because the role already exists into the DB.
		int affectedRows = roleDAO.createRole(dbConnection, role.getName());
		assertEquals(0, affectedRows);
	}

	@Test
	public void testGetAllRolesNames_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRolesTable(dbConnection));
		roleDAO.createRole(dbConnection, role.getName());
		assertEquals(1, roleDAO.countRolesTable(dbConnection));

		List<String> rolesNames = roleDAO.getAllRolesNames(dbConnection);

		assertEquals(1, rolesNames.size());
		assertEquals(role.getName(), rolesNames.get(0));

		Role secondRole = RoleFactoryForTests.getDefaultTestRole(2);
		roleDAO.createRole(dbConnection, secondRole.getName());
		assertEquals(2, roleDAO.countRolesTable(dbConnection));

		rolesNames = roleDAO.getAllRolesNames(dbConnection);
		assertEquals(2, rolesNames.size());
	}

	@Test
	public void testGetAllRolesNames_NOK() {


		List<String> rolesNames = roleDAO.getAllRolesNames(dbConnection);
		assertEquals(0, rolesNames.size());
	}

	@Test
	public void testDeleteRole_OK() {

		Role defaultRole = RoleFactoryForTests.getDefaultTestRole();
		Role secondRole = RoleFactoryForTests.getDefaultTestRole(2);

		assertEquals(0, roleDAO.countRolesTable(dbConnection));
		roleDAO.createRole(dbConnection, defaultRole.getName());
		roleDAO.createRole(dbConnection, secondRole.getName());
		assertEquals(2, roleDAO.countRolesTable(dbConnection));

		roleDAO.deleteRole(dbConnection, defaultRole.getName());
		assertEquals(1, roleDAO.countRolesTable(dbConnection));

		List<String> allRolesNames = roleDAO.getAllRolesNames(dbConnection);
		assertEquals(1, allRolesNames.size());
		assertEquals(secondRole.getName(), allRolesNames.get(0));

		roleDAO.deleteRole(dbConnection, secondRole.getName());
		assertEquals(0, roleDAO.countRolesTable(dbConnection));
	}

	@Test
	public void testDeleteRole_NOK() {

		String fakeRoleName = "fakeRoleName";

		assertEquals(0, roleDAO.countRolesTable(dbConnection));
		roleDAO.deleteRole(dbConnection, fakeRoleName);
		assertEquals(0, roleDAO.countRolesTable(dbConnection));
	}

	@Test
	public void testAddUserToRole_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRoleUsersTable(dbConnection));
		roleDAO.addUserToRole(dbConnection, user.getUsername(), role.getName());
		assertEquals(1, roleDAO.countRoleUsersTable(dbConnection));

		String secondUsername = "second";
		String thirdUsername = "third";

		roleDAO.addUserToRole(dbConnection, secondUsername, role.getName());
		roleDAO.addUserToRole(dbConnection, thirdUsername, role.getName());
		assertEquals(3, roleDAO.countRoleUsersTable(dbConnection));
	}

	@Test
	public void testAddUserToRole_NOK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRoleUsersTable(dbConnection));
		roleDAO.addUserToRole(dbConnection, user.getUsername(), role.getName());
		assertEquals(1, roleDAO.countRoleUsersTable(dbConnection));

		// The method should return 0 because the username is already been assigned a role.
		int affectedRows = roleDAO.addUserToRole(dbConnection, user.getUsername(), role.getName());
		assertEquals(0, affectedRows);
	}

	@Test
	public void testRemoveUserFromRole_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRoleUsersTable(dbConnection));
		roleDAO.addUserToRole(dbConnection, user.getUsername(), role.getName());
		assertEquals(1, roleDAO.countRoleUsersTable(dbConnection));

		String secondUsername = "second";
		String thirdUsername = "third";

		roleDAO.addUserToRole(dbConnection, secondUsername, role.getName());
		roleDAO.addUserToRole(dbConnection, thirdUsername, role.getName());
		assertEquals(3, roleDAO.countRoleUsersTable(dbConnection));

		roleDAO.removeUserFromRole(dbConnection, secondUsername);
		assertEquals(2, roleDAO.countRoleUsersTable(dbConnection));

		// The same username so nothing changes.
		roleDAO.removeUserFromRole(dbConnection, secondUsername);
		assertEquals(2, roleDAO.countRoleUsersTable(dbConnection));

		roleDAO.removeUserFromRole(dbConnection, user.getUsername());
		assertEquals(1, roleDAO.countRoleUsersTable(dbConnection));

		roleDAO.removeUserFromRole(dbConnection, thirdUsername);
		assertEquals(0, roleDAO.countRoleUsersTable(dbConnection));
	}

	@Test
	public void testRemoveUserFromRole_NOK() {

		String fakeUsername = "fakeUsername";

		assertEquals(0, roleDAO.countRoleUsersTable(dbConnection));
		roleDAO.removeUserFromRole(dbConnection, fakeUsername);
		assertEquals(0, roleDAO.countRoleUsersTable(dbConnection));
	}

	@Test
	public void testRemoveAllUsersFromRole_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRoleUsersTable(dbConnection));
		roleDAO.addUserToRole(dbConnection, user.getUsername(), role.getName());
		assertEquals(1, roleDAO.countRoleUsersTable(dbConnection));

		String secondUsername = "second";
		String thirdUsername = "third";

		roleDAO.addUserToRole(dbConnection, secondUsername, role.getName());
		roleDAO.addUserToRole(dbConnection, thirdUsername, role.getName());
		assertEquals(3, roleDAO.countRoleUsersTable(dbConnection));

		roleDAO.removeAllUsersFromRole(dbConnection, role.getName());
		assertEquals(0, roleDAO.countRoleUsersTable(dbConnection));
	}

	@Test
	public void testRemoveAllUsersFromRole_NOK() {

		User user = UserFactoryForTests.getDefaultTestUser();
		Role role = RoleFactoryForTests.getDefaultTestRole();

		assertEquals(0, roleDAO.countRoleUsersTable(dbConnection));
		roleDAO.addUserToRole(dbConnection, user.getUsername(), role.getName());
		assertEquals(1, roleDAO.countRoleUsersTable(dbConnection));

		String secondUsername = "second";
		String thirdUsername = "third";

		roleDAO.addUserToRole(dbConnection, secondUsername, role.getName());
		roleDAO.addUserToRole(dbConnection, thirdUsername, role.getName());
		assertEquals(3, roleDAO.countRoleUsersTable(dbConnection));

		String fakeRoleName = "fakeRoleName";
		roleDAO.removeAllUsersFromRole(dbConnection, fakeRoleName);
		assertEquals(3, roleDAO.countRoleUsersTable(dbConnection));
	}
}