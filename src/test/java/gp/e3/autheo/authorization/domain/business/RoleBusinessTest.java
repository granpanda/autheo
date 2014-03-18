package gp.e3.autheo.authorization.domain.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.authorization.infrastructure.dtos.PermissionTuple;
import gp.e3.autheo.authorization.persistence.daos.IRoleDAO;
import gp.e3.autheo.util.RoleFactoryForTests;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import redis.clients.jedis.Jedis;

public class RoleBusinessTest {

	private IRoleDAO roleDaoMock;
	private PermissionBusiness permissionBusinessMock;
	private Jedis redisClientMock;

	private RoleBusiness roleBusiness;

	@Before
	public void setUp() {

		roleDaoMock = Mockito.mock(IRoleDAO.class);
		permissionBusinessMock =  Mockito.mock(PermissionBusiness.class);
		redisClientMock = Mockito.mock(Jedis.class);

		roleBusiness = new RoleBusiness(roleDaoMock, permissionBusinessMock, redisClientMock);
	}

	@After
	public void tearDown() {

		roleDaoMock = null;
		permissionBusinessMock = null;
		redisClientMock = null;

		roleBusiness = null;
	}

	@Test
	public void testCreateRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		try {

			Role createdRole = roleBusiness.createRole(role);

			assertNotNull(createdRole);
			assertEquals(role.getName(), createdRole.getName());

		} catch (DuplicateIdException e) {

			fail("The method should not throw an exception because the role was not into the database.");
		}
	}

	@Test
	public void testCreateRole_NOK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		try {

			String errorMessage = "Duplicated primary key: " + role.getName();
			Mockito.doNothing().doThrow(new DuplicateIdException(errorMessage)).when(roleDaoMock).createRole(role.getName());

			Role createdRole = roleBusiness.createRole(role);

			assertNotNull(createdRole);
			assertEquals(role.getName(), createdRole.getName());

			roleBusiness.createRole(role);
			fail("The method should throw an exception because the role was into the database.");

		} catch (DuplicateIdException e) {

			assertNotNull(e);

		} catch (Exception e) {

			assertNotNull(e);
		}
	}

	@Test
	public void testGetRoleByName_OK() {

		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);

		Mockito.when(permissionBusinessMock.getAllPermissionsOfAGivenRole(role.getName())).thenReturn(role.getPermissions());

		Role retrievedRole = roleBusiness.getRoleByName(role.getName());

		assertNotNull(retrievedRole);

		assertEquals(role.getName(), retrievedRole.getName());
		assertEquals(listSize, role.getPermissions().size());
		assertEquals(role.getPermissions().size(), retrievedRole.getPermissions().size());
	}

	@Test
	public void testGetRoleByName_NOK() {

		int listSize = 0;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);

		Mockito.when(permissionBusinessMock.getAllPermissionsOfAGivenRole(role.getName())).thenReturn(role.getPermissions());

		Role retrievedRole = roleBusiness.getRoleByName(role.getName());

		assertNotNull(retrievedRole);

		assertEquals(role.getName(), retrievedRole.getName());
		assertEquals(listSize, role.getPermissions().size());
		assertEquals(role.getPermissions().size(), retrievedRole.getPermissions().size());
	}

	@Test
	public void testGetAllRolesNames_OK() {

		List<String> rolesNamesList = new ArrayList<String>();

		rolesNamesList.add("admin");
		rolesNamesList.add("tester");

		Mockito.when(roleDaoMock.getAllRolesNames()).thenReturn(rolesNamesList);

		List<String> allRolesNames = roleBusiness.getAllRolesNames();

		assertNotNull(allRolesNames);
		assertEquals(rolesNamesList.size(), allRolesNames.size());
	}

	@Test
	public void testGetAllRolesNames_NOK() {

		List<String> rolesNamesList = new ArrayList<String>();

		Mockito.when(roleDaoMock.getAllRolesNames()).thenReturn(rolesNamesList);

		List<String> allRolesNames = roleBusiness.getAllRolesNames();

		assertNotNull(allRolesNames);
		assertEquals(rolesNamesList.size(), allRolesNames.size());
	}

	@Test
	public void testGetAllPermissionsOfAGivenRole_OK() {

		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);

		Mockito.when(permissionBusinessMock.getAllPermissionsOfAGivenRole(role.getName())).thenReturn(role.getPermissions());

		List<Permission> rolePermissions = roleBusiness.getAllPermissionsOfAGivenRole(role.getName());

		assertNotNull(rolePermissions);
		assertEquals(role.getPermissions().size(), rolePermissions.size());
	}

	@Test
	public void testGetAllPermissionsOfAGivenRole_NOK() {

		int listSize = 0;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);

		Mockito.when(permissionBusinessMock.getAllPermissionsOfAGivenRole(role.getName())).thenReturn(role.getPermissions());

		List<Permission> rolePermissions = roleBusiness.getAllPermissionsOfAGivenRole(role.getName());

		assertNotNull(rolePermissions);
		assertEquals(role.getPermissions().size(), rolePermissions.size());
	}

	@Test
	public void testRolePermissionsAreInRedis_OK() {

		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);
		String rolePermissionsToString = role.getPermissions().toString();

		Mockito.when(redisClientMock.get(role.getName())).thenReturn(rolePermissionsToString);

		assertTrue(roleBusiness.rolePermissionsAreInRedis(role.getName()));
	}

	@Test
	public void testRolePermissionsAreInRedis_NOK() {

		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);

		Mockito.when(redisClientMock.get(role.getName())).thenReturn(null);

		assertFalse(roleBusiness.rolePermissionsAreInRedis(role.getName()));
	}

	private String getRolePermissionsAsStringToRedis(List<Permission> rolePermissions) {

		String permissionsString = "";

		for (Permission permission : rolePermissions) {

			permissionsString += permission.toString() + Permission.PERMISSION_SPLIT;
		}

		return permissionsString;
	}
	
	@Test
	public void testAddRolePermissionsToRedis_OK() {

		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);

		List<Permission> rolePermissions = role.getPermissions();
		String rolePermissionsToString = getRolePermissionsAsStringToRedis(rolePermissions);

		Mockito.when(permissionBusinessMock.getAllPermissionsOfAGivenRole(role.getName())).thenReturn(rolePermissions);
		Mockito.when(redisClientMock.set(role.getName(), rolePermissionsToString)).thenReturn("OK");

		assertTrue(roleBusiness.addRolePermissionsToRedis(role.getName()));
	}

	@Test
	public void testAddRolePermissionsToRedis_NOK() {

		int listSize = 0;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);
		Mockito.when(permissionBusinessMock.getAllPermissionsOfAGivenRole(role.getName())).thenReturn(role.getPermissions());

		assertFalse(roleBusiness.addRolePermissionsToRedis(role.getName()));
	}

	@Test
	public void testGetRolePermissionsFromRedis_OK() {

		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);
		String rolePermissionsToString = getRolePermissionsAsStringToRedis(role.getPermissions());

		Mockito.when(redisClientMock.get(role.getName())).thenReturn(rolePermissionsToString);

		List<PermissionTuple> retrievedRolePermissions = roleBusiness.getRolePermissionsFromRedis(role.getName());

		assertNotNull(retrievedRolePermissions);
		assertEquals(listSize, role.getPermissions().size());
		assertEquals(role.getPermissions().size(), retrievedRolePermissions.size());
	}

	@Test
	public void testGetRolePermissionsFromRedis_NOK() {

		int listSize = 0;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);
		String rolePermissionsToString = getRolePermissionsAsStringToRedis(role.getPermissions());

		Mockito.when(redisClientMock.get(role.getName())).thenReturn(rolePermissionsToString);

		List<PermissionTuple> retrievedRolePermissions = roleBusiness.getRolePermissionsFromRedis(role.getName());

		assertNotNull(retrievedRolePermissions);
		assertEquals(listSize, role.getPermissions().size());
		assertEquals(role.getPermissions().size(), retrievedRolePermissions.size());
	}
}