package gp.e3.autheo.authorization.persistence.daos;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.util.PermissionFactoryForTests;
import gp.e3.autheo.util.RoleFactoryForTests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class PermissionDAOTest {

	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static DBI dbi;
	private static Handle handle;

	private static IRoleDAO roleDAO;
	private static IPermissionDAO permissionDAO;

	@BeforeClass
	public static void setUpClass() {

		dbi = new DBI(H2_IN_MEMORY_DB);
		handle = dbi.open();

		roleDAO = handle.attach(IRoleDAO.class);
		permissionDAO = handle.attach(IPermissionDAO.class);
	}

	@AfterClass
	public static void tearDownClass() {

		handle.close();
		dbi.close(roleDAO);
		dbi.close(permissionDAO);

		roleDAO = null;
		permissionDAO = null;

		dbi = null;
	}

	@Before
	public void setUp() {

		roleDAO.createRolesAndPermissionsTable();

		permissionDAO.createPermissionsTable();
		permissionDAO.createPermissionsUniqueIndex();
	}

	@After
	public void tearDown() {

		handle.execute("DROP TABLE roles_permissions");
		handle.execute("DROP TABLE permissions");
	}

	@Test
	public void testCreatePermission_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			assertEquals(0, permissionDAO.countPermissionsTable());
			int generatedId = permissionDAO.createPermission(permission.getName(), permission.getHttpVerb(), permission.getUrl());
			assertEquals(1, permissionDAO.countPermissionsTable());

			Permission retrievedPermission = permissionDAO.getPermissionById(generatedId);

			assertEquals(0, permission.compareTo(retrievedPermission));

		} catch (Exception e) {

			showDefaultFailMessage();
		}
	}

	@Test
	public void testCreatePermission_NOK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			assertEquals(0, permissionDAO.countPermissionsTable());
			permissionDAO.createPermission(permission.getName(), permission.getHttpVerb(), permission.getUrl());
			assertEquals(1, permissionDAO.countPermissionsTable());

			permissionDAO.createPermission(permission.getName(), permission.getHttpVerb(), permission.getUrl());

			fail("The method should fail because the http verb and the url already exists into the DB.");

		} catch (Exception e) {

			assertNotNull(e);
		}
	}

	@Test
	public void testAssociateAllPermissionsToRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		List<Permission> permissions = PermissionFactoryForTests.getPermissionList(5);
		List<Integer> permissionsIds = getPermissionsIdsAsList(permissions);

		assertEquals(0, permissionDAO.countRolePermissionsTable());
		permissionDAO.associateAllPermissionsToRole(role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable());
	}

	@Test
	public void testAssociateAllPermissionsToRole_NOK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		List<Integer> permissionsIds = new ArrayList<Integer>();

		assertEquals(0, permissionDAO.countRolePermissionsTable());
		permissionDAO.associateAllPermissionsToRole(role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable());
	}

	@Test
	public void testDisassociateAllPermissionsFromRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		List<Permission> permissions = PermissionFactoryForTests.getPermissionList(5);
		List<Integer> permissionsIds = getPermissionsIdsAsList(permissions);

		assertEquals(0, permissionDAO.countRolePermissionsTable());
		permissionDAO.associateAllPermissionsToRole(role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable());

		permissionDAO.disassociateAllPermissionsFromRole(role.getName());
		assertEquals(0, permissionDAO.countRolePermissionsTable());
	}

	@Test
	public void testDisassociateAllPermissionsFromRole_NOK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		List<Integer> permissionsIds = new ArrayList<Integer>();

		assertEquals(0, permissionDAO.countRolePermissionsTable());
		permissionDAO.associateAllPermissionsToRole(role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable());

		permissionDAO.disassociateAllPermissionsFromRole(role.getName());
		assertEquals(0, permissionDAO.countRolePermissionsTable());
	}

	@Test
	public void testDisassociatePermissionFromAllRoles_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		List<Permission> permissions = PermissionFactoryForTests.getPermissionList(5);
		List<Integer> permissionsIds = getPermissionsIdsAsList(permissions);

		assertEquals(0, permissionDAO.countRolePermissionsTable());
		permissionDAO.associateAllPermissionsToRole(role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable());

		permissionDAO.disassociatePermissionFromAllRoles(permissionsIds.get(0));
		assertEquals(permissionsIds.size() - 1, permissionDAO.countRolePermissionsTable());

		permissionDAO.disassociatePermissionFromAllRoles(permissionsIds.get(1));
		assertEquals(permissionsIds.size() - 2, permissionDAO.countRolePermissionsTable());
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

		assertEquals(0, permissionDAO.countRolePermissionsTable());
		permissionDAO.associateAllPermissionsToRole(role.getName(), permissionsIds);
		assertEquals(permissionsIds.size(), permissionDAO.countRolePermissionsTable());

		int fakePermissionId = 0;
		permissionDAO.disassociatePermissionFromAllRoles(fakePermissionId);
		assertEquals(0, permissionDAO.countRolePermissionsTable());

		permissionDAO.disassociatePermissionFromAllRoles(fakePermissionId);
		assertEquals(0, permissionDAO.countRolePermissionsTable());
	}

	@Test
	public void testGetPermissionById_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			int generatedId = permissionDAO.createPermission(permission.getName(), permission.getHttpVerb(), permission.getUrl());
			Permission retrievedPermission = permissionDAO.getPermissionById(generatedId);
			assertEquals(0, permission.compareTo(retrievedPermission));

		} catch (Exception e) {

			showDefaultFailMessage();
		}
	}

	@Test
	public void testGetPermissionById_NOK() {

		try {

			int fakePermissionId = 0;
			Permission retrievedPermission = permissionDAO.getPermissionById(fakePermissionId);
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

			int generatedId = permissionDAO.createPermission(permission.getName(), permission.getHttpVerb(), permission.getUrl());
			Permission retrievedPermission = permissionDAO.getPermissionByHttpVerbAndUrl(permission.getHttpVerb(), permission.getUrl());

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
		Permission retrievedPermission = permissionDAO.getPermissionByHttpVerbAndUrl(fakeHttpVerb, fakeUrl);

		assertNull(retrievedPermission);
	}

	@Test
	public void testGetAllPermissions_OK() {

		try {

			int listSize = 5;
			List<Permission> permissionList = PermissionFactoryForTests.getPermissionList(listSize);

			assertEquals(0, permissionDAO.countPermissionsTable());

			createMultiplePermissions(permissionList);

			assertEquals(listSize, permissionDAO.countPermissionsTable());

			List<Permission> allPermissions = permissionDAO.getAllPermissions();
			assertEquals(permissionDAO.countPermissionsTable(), allPermissions.size());

		} catch (Exception e) {

			showDefaultFailMessage();
		}
	}

	private void createMultiplePermissions(List<Permission> permissionList)
			throws Exception {
		for (int i = 0; i < permissionList.size(); i++) {

			Permission permission = permissionList.get(i);
			permissionDAO.createPermission(permission.getName(), permission.getHttpVerb(), permission.getUrl());
		}
	}

	@Test
	public void testGetAllPermissions_NOK() {

		assertEquals(0, permissionDAO.countPermissionsTable());

		List<Permission> allPermissions = permissionDAO.getAllPermissions();
		assertEquals(permissionDAO.countPermissionsTable(), allPermissions.size());
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
		
		permissionDAO.associateAllPermissionsToRole(adminRole.getName(), adminPermissionsIds);
		
		assertEquals(adminNumberOfPermissions, permissionDAO.countRolePermissionsTable());
		
		List<Permission> retrievedAdminPermissions = permissionDAO.getAllPermissionsOfAGivenRole(adminRole.getName());
		assertEquals(adminNumberOfPermissions, retrievedAdminPermissions.size());
	}
	
	@Test
	public void testGetAllPermissionsOfAGivenRole_NOK() {
		
		int adminNumberOfPermissions = 5;
		Role adminRole = RoleFactoryForTests.getDefaultTestRole(adminNumberOfPermissions);
		List<Integer> adminPermissionsIds = getPermissionsIdsAsList(adminRole.getPermissions());
		
		permissionDAO.associateAllPermissionsToRole(adminRole.getName(), adminPermissionsIds);
		
		assertEquals(adminNumberOfPermissions, permissionDAO.countRolePermissionsTable());
		
		List<Permission> retrievedAdminPermissions = permissionDAO.getAllPermissionsOfAGivenRole(adminRole.getName());
		
		// Should return an empty list because the roles are not created into the db.
		assertEquals(0, retrievedAdminPermissions.size());
	}
	
	@Test
	public void testDeletePermission_OK() {
		
		Permission firstPermission = PermissionFactoryForTests.getDefaultTestPermission(1);
		Permission secondPermission = PermissionFactoryForTests.getDefaultTestPermission(2);
		
		try {
			
			int firstPermissionId = permissionDAO.createPermission(firstPermission.getName(), firstPermission.getHttpVerb(), firstPermission.getUrl());
			int secondPermissionId = permissionDAO.createPermission(secondPermission.getName(), secondPermission.getHttpVerb(), secondPermission.getUrl());
			
			assertEquals(2, permissionDAO.countPermissionsTable());
			
			permissionDAO.deletePermission(firstPermissionId);
			
			assertEquals(1, permissionDAO.countPermissionsTable());
			assertNull(permissionDAO.getPermissionById(firstPermissionId));
			
			permissionDAO.deletePermission(secondPermissionId);
			
			assertEquals(0, permissionDAO.countPermissionsTable());
			assertNull(permissionDAO.getPermissionById(secondPermissionId));
			
		} catch (Exception e) {
			
			showDefaultFailMessage();
		}
	}
	
	@Test
	public void testDeletePermission_NOK() {
		
		Permission firstPermission = PermissionFactoryForTests.getDefaultTestPermission(1);
		
		try {
			
			int firstPermissionId = permissionDAO.createPermission(firstPermission.getName(), firstPermission.getHttpVerb(), firstPermission.getUrl());
			
			assertEquals(1, permissionDAO.countPermissionsTable());
			
			int fakePermissionId = 123;
			permissionDAO.deletePermission(fakePermissionId);
			
			assertEquals(1, permissionDAO.countPermissionsTable());
			
			permissionDAO.deletePermission(firstPermissionId);
			
			assertEquals(0, permissionDAO.countPermissionsTable());
			assertNull(permissionDAO.getPermissionById(firstPermissionId));
			
		} catch (Exception e) {
			
			showDefaultFailMessage();
		}
	}
}