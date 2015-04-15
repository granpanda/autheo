package gp.e3.autheo.authorization.domain.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.authorization.infrastructure.dtos.PermissionTuple;
import gp.e3.autheo.authorization.persistence.daos.RoleDAO;
import gp.e3.autheo.util.ExceptionUtilsForTests;
import gp.e3.autheo.util.RoleFactoryForTests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RoleBusinessTest {

	private Connection dbConnectionMock;
	private BasicDataSource dataSourceMock;

	private Jedis redisMock;
	private JedisPool redisPoolMock;

	private RoleDAO roleDaoMock;
	private PermissionBusiness permissionBusinessMock;

	private RoleBusiness roleBusiness;

	@Before
	public void setUp() {

		dbConnectionMock = Mockito.mock(Connection.class);
		dataSourceMock = Mockito.mock(BasicDataSource.class);

		try {
			Mockito.when(dataSourceMock.getConnection()).thenReturn(dbConnectionMock);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		redisMock = Mockito.mock(Jedis.class);
		redisPoolMock = Mockito.mock(JedisPool.class);
		Mockito.when(redisPoolMock.getResource()).thenReturn(redisMock);

		roleDaoMock = Mockito.mock(RoleDAO.class);
		permissionBusinessMock =  Mockito.mock(PermissionBusiness.class);

		roleBusiness = new RoleBusiness(dataSourceMock, redisPoolMock, roleDaoMock, permissionBusinessMock);
	}

	@After
	public void tearDown() {

		dbConnectionMock = null;
		dataSourceMock = null;

		redisMock = null;
		redisPoolMock = null;

		roleDaoMock = null;
		permissionBusinessMock = null;

		roleBusiness = null;
	}

	@Test
	public void testCreateRole_OK() {
		
		try {
			
			Role role = RoleFactoryForTests.getDefaultTestRole();
			
			int affectedRows = 1;
			Mockito.when(roleDaoMock.createRole(dbConnectionMock, role.getName())).thenReturn(affectedRows);
			Role createdRole = roleBusiness.createRole(role);

			assertNotNull(createdRole);
			assertEquals(role.getName(), createdRole.getName());
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testCreateRole_NOK() {
		
		try {
			
			Role role = RoleFactoryForTests.getDefaultTestRole();
			int affectedRows = 0;
			Mockito.when(roleDaoMock.createRole(dbConnectionMock, role.getName())).thenReturn(affectedRows);

			Role createdRole = roleBusiness.createRole(role);
			assertNull(createdRole);
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
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
		
		try {
			
			List<String> rolesNamesList = new ArrayList<String>();

			rolesNamesList.add("admin");
			rolesNamesList.add("tester");
			Mockito.when(roleDaoMock.getAllRolesNames(dbConnectionMock)).thenReturn(rolesNamesList);

			List<String> allRolesNames = roleBusiness.getAllRolesNames();

			assertNotNull(allRolesNames);
			assertEquals(rolesNamesList.size(), allRolesNames.size());
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
	}

	@Test
	public void testGetAllRolesNames_NOK() {
		
		try {
			
			List<String> rolesNamesList = new ArrayList<String>();
			Mockito.when(roleDaoMock.getAllRolesNames(dbConnectionMock)).thenReturn(rolesNamesList);

			List<String> allRolesNames = roleBusiness.getAllRolesNames();

			assertNotNull(allRolesNames);
			assertEquals(rolesNamesList.size(), allRolesNames.size());
			
		} catch (SQLException e) {
			
			ExceptionUtilsForTests.logAndFailOnUnexpectedException(e);
		}
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

		Mockito.when(redisMock.get(role.getName())).thenReturn(rolePermissionsToString);

		assertTrue(roleBusiness.rolePermissionsAreInRedis(role.getName()));
	}

	@Test
	public void testRolePermissionsAreInRedis_NOK() {

		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);

		Mockito.when(redisMock.get(role.getName())).thenReturn(null);

		assertFalse(roleBusiness.rolePermissionsAreInRedis(role.getName()));
	}

	private String getRolePermissionsAsStringToRedis(List<Permission> rolePermissions) {
		
		Gson gson = new Gson();
		return gson.toJson(rolePermissions);
	}

	@Test
	public void testAddRolePermissionsToRedis_OK() {

		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);

		List<Permission> rolePermissions = role.getPermissions();
		String rolePermissionsToString = getRolePermissionsAsStringToRedis(rolePermissions);

		Mockito.when(permissionBusinessMock.getAllPermissionsOfAGivenRole(role.getName())).thenReturn(rolePermissions);
		Mockito.when(redisMock.set(role.getName(), rolePermissionsToString)).thenReturn("OK");

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

		Mockito.when(redisMock.get(role.getName())).thenReturn(rolePermissionsToString);

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

		Mockito.when(redisMock.get(role.getName())).thenReturn(rolePermissionsToString);

		List<PermissionTuple> retrievedRolePermissions = roleBusiness.getRolePermissionsFromRedis(role.getName());

		assertNotNull(retrievedRolePermissions);
		assertEquals(listSize, role.getPermissions().size());
		assertEquals(role.getPermissions().size(), retrievedRolePermissions.size());
	}
}