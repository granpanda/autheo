package gp.e3.autheo.authorization.persistence.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.infrastructure.utils.SqlUtils;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.util.PermissionFactoryForTests;
import gp.e3.autheo.util.RoleFactoryForTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PermissionDAOTest {

	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static Connection dbConnection;
	private static RoleDAO roleDAO;
	private static PermissionDAO permissionDAO;

	@BeforeClass
	public static void setUpClass() {

		try {

			Class.forName("org.h2.Driver");
			dbConnection = DriverManager.getConnection(H2_IN_MEMORY_DB);

			roleDAO = new RoleDAO();
			permissionDAO = new PermissionDAO();

		} catch (ClassNotFoundException | SQLException e) {

			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownClass() {

		roleDAO = null;
		permissionDAO = null;
		SqlUtils.closeDbConnection(dbConnection);
	}

	@Before
	public void setUp() {

		roleDAO.createRolesAndPermissionsTable(dbConnection);
		permissionDAO.createPermissionsTable(dbConnection);
	}

	@After
	public void tearDown() {

		try {

			String dropRolesAndPermissionsTable = "DROP TABLE roles_permissions";
			PreparedStatement prepareStatement = dbConnection.prepareStatement(dropRolesAndPermissionsTable);
			prepareStatement.executeUpdate();
			prepareStatement.close();
			prepareStatement = null;

			String dropPermissionsTable = "DROP TABLE permissions";
			PreparedStatement prepareStatement2 = dbConnection.prepareStatement(dropPermissionsTable);
			prepareStatement2.executeUpdate();
			prepareStatement2.close();
			prepareStatement2 = null;

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	@Test
	public void testCreatePermission_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			assertEquals(0, permissionDAO.countPermissionsTable(dbConnection));
			long generatedId = permissionDAO.createPermission(dbConnection, permission);
			assertEquals(1, permissionDAO.countPermissionsTable(dbConnection));

			assertNotEquals(0, generatedId);
			Permission retrievedPermission = permissionDAO.getPermissionById(dbConnection, generatedId);
			assertEquals(0, permission.compareTo(retrievedPermission));

		} catch (Exception e) {

			showDefaultFailMessage();
		}
	}

	@Test
	public void testCreatePermission_NOK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			assertEquals(0, permissionDAO.countPermissionsTable(dbConnection));
			long firstPermissionId = permissionDAO.createPermission(dbConnection, permission);
			assertEquals(1, permissionDAO.countPermissionsTable(dbConnection));
			assertNotEquals(0, firstPermissionId);

			long secondPermissionId = permissionDAO.createPermission(dbConnection, permission);
			assertEquals(0, secondPermissionId);
			assertEquals(1, permissionDAO.countPermissionsTable(dbConnection));

		} catch (Exception e) {

			assertNotNull(e);
		}
	}

	@Test
	public void testAssociateAllPermissionsToRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		List<Permission> permissions = PermissionFactoryForTests.getPermissionList(5);
		List<Integer> permissionsIds = getPermissionsIdsAsList(permissions);

		assertEquals(0, permissionDAO.countRolePermissionsTable(dbConnection));
		permissionDAO.associateAllPermissionsToRole(dbConnection, role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable(dbConnection));
	}

	@Test
	public void testAssociateAllPermissionsToRole_NOK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		List<Integer> permissionsIds = new ArrayList<Integer>();

		assertEquals(0, permissionDAO.countRolePermissionsTable(dbConnection));
		permissionDAO.associateAllPermissionsToRole(dbConnection, role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable(dbConnection));
	}

	@Test
	public void testDisassociateAllPermissionsFromRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		List<Permission> permissions = PermissionFactoryForTests.getPermissionList(5);
		List<Integer> permissionsIds = getPermissionsIdsAsList(permissions);

		assertEquals(0, permissionDAO.countRolePermissionsTable(dbConnection));
		permissionDAO.associateAllPermissionsToRole(dbConnection, role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable(dbConnection));

		permissionDAO.disassociateAllPermissionsFromRole(dbConnection, role.getName());
		assertEquals(0, permissionDAO.countRolePermissionsTable(dbConnection));
	}

	@Test
	public void testDisassociateAllPermissionsFromRole_NOK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		List<Integer> permissionsIds = new ArrayList<Integer>();

		assertEquals(0, permissionDAO.countRolePermissionsTable(dbConnection));
		permissionDAO.associateAllPermissionsToRole(dbConnection, role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable(dbConnection));

		permissionDAO.disassociateAllPermissionsFromRole(dbConnection, role.getName());
		assertEquals(0, permissionDAO.countRolePermissionsTable(dbConnection));
	}

	@Test
	public void testDisassociatePermissionFromAllRoles_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		List<Permission> permissions = PermissionFactoryForTests.getPermissionList(5);
		List<Integer> permissionsIds = getPermissionsIdsAsList(permissions);

		assertEquals(0, permissionDAO.countRolePermissionsTable(dbConnection));
		permissionDAO.associateAllPermissionsToRole(dbConnection, role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable(dbConnection));

		permissionDAO.disassociatePermissionFromAllRoles(dbConnection, permissionsIds.get(0));
		assertEquals(permissionsIds.size() - 1, permissionDAO.countRolePermissionsTable(dbConnection));

		permissionDAO.disassociatePermissionFromAllRoles(dbConnection, permissionsIds.get(1));
		assertEquals(permissionsIds.size() - 2, permissionDAO.countRolePermissionsTable(dbConnection));
	}

	private List<Integer> getPermissionsIdsAsList(List<Permission> permissions) {
		List<Integer> permissionsIds = new ArrayList<Integer>();

		for (int i = 0; i < permissions.size(); i++) {
			permissionsIds.add(permissions.get(i).getId());
		}
		return permissionsIds;
	}

	@Test
	public void testDisassociatePermissionFromAllRoles_NOK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		List<Integer> permissionsIds = new ArrayList<Integer>();

		assertEquals(0, permissionDAO.countRolePermissionsTable(dbConnection));
		permissionDAO.associateAllPermissionsToRole(dbConnection, role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable(dbConnection));

		int fakePermissionId = 0;
		permissionDAO.disassociatePermissionFromAllRoles(dbConnection, fakePermissionId);
		assertEquals(0, permissionDAO.countRolePermissionsTable(dbConnection));

		permissionDAO.disassociatePermissionFromAllRoles(dbConnection, fakePermissionId);
		assertEquals(0, permissionDAO.countRolePermissionsTable(dbConnection));
	}

	@Test
	public void testGetPermissionById_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			long generatedId = permissionDAO.createPermission(dbConnection, permission);
			Permission retrievedPermission = permissionDAO.getPermissionById(dbConnection, generatedId);
			assertEquals(0, permission.compareTo(retrievedPermission));

		} catch (Exception e) {

			showDefaultFailMessage();
		}
	}

	@Test
	public void testGetPermissionById_NOK() {

		try {

			int fakePermissionId = 0;
			Permission retrievedPermission = permissionDAO.getPermissionById(dbConnection, fakePermissionId);
			assertNull(retrievedPermission);

		} catch (Exception e) {

			showDefaultFailMessage();
		}
	}

	private void showDefaultFailMessage() {
		fail("The method sould not throw an exception.");
	}

	@Test
	public void testGetPermissionByHttpVerbAndUrl_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			long generatedId = permissionDAO.createPermission(dbConnection, permission);
			Permission retrievedPermission = permissionDAO.getPermissionByHttpVerbAndUrl(dbConnection, permission.getHttpVerb(), permission.getUrl());

			assertEquals(generatedId, retrievedPermission.getId());
			assertEquals(0, permission.compareTo(retrievedPermission));

		} catch (Exception e) {

			showDefaultFailMessage();
		}
	}

	@Test
	public void testGetPermissionByHttpVerbAndUrl_NOK() {

		String fakeHttpVerb = "FAKE";
		String fakeUrl = "www.fake.com";
		Permission retrievedPermission = permissionDAO.getPermissionByHttpVerbAndUrl(dbConnection, fakeHttpVerb, fakeUrl);

		assertNull(retrievedPermission);
	}

	@Test
	public void testGetAllPermissions_OK() {

		try {

			int listSize = 5;
			List<Permission> permissionList = PermissionFactoryForTests.getPermissionList(listSize);

			assertEquals(0, permissionDAO.countPermissionsTable(dbConnection));

			createMultiplePermissions(permissionList);

			assertEquals(listSize, permissionDAO.countPermissionsTable(dbConnection));

			List<Permission> allPermissions = permissionDAO.getAllPermissions(dbConnection);
			assertEquals(permissionDAO.countPermissionsTable(dbConnection), allPermissions.size());

		} catch (Exception e) {

			showDefaultFailMessage();
		}
	}

	private void createMultiplePermissions(List<Permission> permissionList)
			throws Exception {

		for (int i = 0; i < permissionList.size(); i++) {

			Permission permission = permissionList.get(i);
			permissionDAO.createPermission(dbConnection, permission);
		}
	}

	@Test
	public void testGetAllPermissions_NOK() {

		assertEquals(0, permissionDAO.countPermissionsTable(dbConnection));

		List<Permission> allPermissions = permissionDAO.getAllPermissions(dbConnection);
		assertEquals(permissionDAO.countPermissionsTable(dbConnection), allPermissions.size());
	}

	@Test
	public void testGetAllPermissionsOfAGivenRole_OK() {

		int adminNumberOfPermissions = 5;
		Role adminRole = RoleFactoryForTests.getDefaultTestRole(adminNumberOfPermissions);
		List<Integer> adminPermissionsIds = getPermissionsIdsAsList(adminRole.getPermissions());
		try {

			createMultiplePermissions(adminRole.getPermissions());

		} catch (Exception e) {

			showDefaultFailMessage();
		}

		permissionDAO.associateAllPermissionsToRole(dbConnection, adminRole.getName(), adminPermissionsIds);

		assertEquals(adminNumberOfPermissions, permissionDAO.countRolePermissionsTable(dbConnection));

		List<Permission> retrievedAdminPermissions = permissionDAO.getAllPermissionsOfAGivenRole(dbConnection, adminRole.getName());
		assertEquals(adminNumberOfPermissions, retrievedAdminPermissions.size());
	}

	@Test
	public void testGetAllPermissionsOfAGivenRole_OK2() {

		int adminNumberOfPermissions = 5;
		int nullNumberOfPermissions = 5;
		List<Permission> nullPermissions = new ArrayList<Permission>();
		nullPermissions.add(PermissionFactoryForTests.getDefaultTestPermission(nullNumberOfPermissions));
		nullNumberOfPermissions++;
		nullPermissions.add(PermissionFactoryForTests.getDefaultTestPermission(nullNumberOfPermissions));
		nullNumberOfPermissions++;
		nullPermissions.add(PermissionFactoryForTests.getDefaultTestPermission(nullNumberOfPermissions));
		nullNumberOfPermissions++;

		Role adminRole = RoleFactoryForTests.getDefaultTestRole(adminNumberOfPermissions);
		List<Integer> adminPermissionsIds = getPermissionsIdsAsList(adminRole.getPermissions());
		try {
			createMultiplePermissions(adminRole.getPermissions());
			createMultiplePermissions(nullPermissions);

		} catch (Exception e) {

			showDefaultFailMessage();
		}

		permissionDAO.associateAllPermissionsToRole(dbConnection, adminRole.getName(), adminPermissionsIds);

		assertEquals(adminNumberOfPermissions, permissionDAO.countRolePermissionsTable(dbConnection));
		assertEquals(nullNumberOfPermissions, permissionDAO.countPermissionsTable(dbConnection));

		List<Permission> retrievedAdminPermissions = permissionDAO.getAllPermissionsOfAGivenRole(dbConnection, adminRole.getName());
		assertEquals(adminNumberOfPermissions, retrievedAdminPermissions.size());
	}

	@Test
	public void testGetAllPermissionsOfAGivenRole_NOK() {

		int adminNumberOfPermissions = 5;
		Role adminRole = RoleFactoryForTests.getDefaultTestRole(adminNumberOfPermissions);
		List<Integer> adminPermissionsIds = getPermissionsIdsAsList(adminRole.getPermissions());

		permissionDAO.associateAllPermissionsToRole(dbConnection, adminRole.getName(), adminPermissionsIds);

		assertEquals(adminNumberOfPermissions, permissionDAO.countRolePermissionsTable(dbConnection));

		List<Permission> retrievedAdminPermissions = permissionDAO.getAllPermissionsOfAGivenRole(dbConnection, adminRole.getName());

		// Should return an empty list because the roles are not created into the db.
		assertEquals(0, retrievedAdminPermissions.size());
	}

	@Test
	public void testDeletePermission_OK() {

		Permission firstPermission = PermissionFactoryForTests.getDefaultTestPermission(1);
		Permission secondPermission = PermissionFactoryForTests.getDefaultTestPermission(2);

		try {

			long firstPermissionId = permissionDAO.createPermission(dbConnection, firstPermission);
			long secondPermissionId = permissionDAO.createPermission(dbConnection, secondPermission);

			assertEquals(2, permissionDAO.countPermissionsTable(dbConnection));

			permissionDAO.deletePermission(dbConnection, firstPermissionId);

			assertEquals(1, permissionDAO.countPermissionsTable(dbConnection));
			assertNull(permissionDAO.getPermissionById(dbConnection, firstPermissionId));

			permissionDAO.deletePermission(dbConnection, secondPermissionId);

			assertEquals(0, permissionDAO.countPermissionsTable(dbConnection));
			assertNull(permissionDAO.getPermissionById(dbConnection, secondPermissionId));

		} catch (Exception e) {

			showDefaultFailMessage();
		}
	}

	@Test
	public void testDeletePermission_NOK() {

		Permission firstPermission = PermissionFactoryForTests.getDefaultTestPermission(1);

		try {

			long firstPermissionId = permissionDAO.createPermission(dbConnection, firstPermission);
			assertEquals(1, permissionDAO.countPermissionsTable(dbConnection));

			int fakePermissionId = 123;
			permissionDAO.deletePermission(dbConnection, fakePermissionId);

			assertEquals(1, permissionDAO.countPermissionsTable(dbConnection));

			permissionDAO.deletePermission(dbConnection, firstPermissionId);

			assertEquals(0, permissionDAO.countPermissionsTable(dbConnection));
			assertNull(permissionDAO.getPermissionById(dbConnection, firstPermissionId));

		} catch (Exception e) {

			showDefaultFailMessage();
		}
	}
}