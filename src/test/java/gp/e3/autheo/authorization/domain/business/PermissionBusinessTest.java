package gp.e3.autheo.authorization.domain.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.authorization.persistence.daos.PermissionDAO;
import gp.e3.autheo.util.PermissionFactoryForTests;
import gp.e3.autheo.util.RoleFactoryForTests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PermissionBusinessTest {

	private Connection dbConnectionMock;
	private BasicDataSource dataSourceMock;
	private PermissionDAO permissionDaoMock;

	private PermissionBusiness permissionBusiness;

	@Before
	public void setUp() {

		dbConnectionMock = Mockito.mock(Connection.class);
		dataSourceMock = Mockito.mock(BasicDataSource.class);
		
		try {
			Mockito.when(dataSourceMock.getConnection()).thenReturn(dbConnectionMock);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		permissionDaoMock = Mockito.mock(PermissionDAO.class);
		permissionBusiness = new PermissionBusiness(dataSourceMock, permissionDaoMock);
	}

	@After
	public void tearDown() {

		dbConnectionMock = null;
		dataSourceMock = null;

		permissionDaoMock = null;
		permissionBusiness = null;
	}

	@Test
	public void testCreatePermission_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		long expectedPermissionId = 1;
		Mockito.when(permissionDaoMock.createPermission(dbConnectionMock, permission)).thenReturn(expectedPermissionId);
		long permissionId = permissionBusiness.createPermission(permission);

		assertNotEquals(0, permissionId);
		assertEquals(expectedPermissionId, permissionId);
	}

	@Test
	public void testCreatePermission_NOK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		long expectedPermissionId = 0;
		Mockito.when(permissionDaoMock.createPermission(dbConnectionMock, permission)).thenReturn(expectedPermissionId);
		long permissionId = permissionBusiness.createPermission(permission);

		assertEquals(0, permissionId);
	}

	@Test
	public void testGetPermissionById_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		Mockito.when(permissionDaoMock.getPermissionById(dbConnectionMock, permission.getId())).thenReturn(permission);
		Permission retrievedPermission = permissionBusiness.getPermissionById(permission.getId());

		assertNotNull(retrievedPermission);
		assertEquals(0, permission.compareTo(retrievedPermission));
	}

	@Test
	public void testGetPermissionById_NOK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		Mockito.when(permissionDaoMock.getPermissionById(dbConnectionMock, permission.getId())).thenReturn(null);
		Permission retrievedPermission = permissionBusiness.getPermissionById(permission.getId());

		assertNull(retrievedPermission);
	}

	@Test
	public void testGetPermissionByHttpVerbAndUrl_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		Mockito.when(permissionDaoMock.getPermissionByHttpVerbAndUrl(dbConnectionMock, permission.getHttpVerb(), permission.getUrl())).thenReturn(permission);
		Permission retrievedPermission = permissionBusiness.getPermissionByHttpVerbAndUrl(permission.getHttpVerb(), permission.getUrl());

		assertNotNull(retrievedPermission);
		assertEquals(0, permission.compareTo(retrievedPermission));
	}

	@Test
	public void testGetPermissionByHttpVerbAndUrl_NOK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		Mockito.when(permissionDaoMock.getPermissionByHttpVerbAndUrl(dbConnectionMock, permission.getHttpVerb(), permission.getUrl())).thenReturn(null);
		Permission retrievedPermission = permissionBusiness.getPermissionByHttpVerbAndUrl(permission.getHttpVerb(), permission.getUrl());

		assertNull(retrievedPermission);
	}

	@Test
	public void testGetAllPermissions_OK() {

		int listSize = 5;
		List<Permission> permissionList = PermissionFactoryForTests.getPermissionList(listSize);
		Mockito.when(permissionDaoMock.getAllPermissions(dbConnectionMock)).thenReturn(permissionList);

		List<Permission> allPermissions = permissionBusiness.getAllPermissions();

		assertNotNull(allPermissions);
		assertEquals(listSize, allPermissions.size());
	}

	@Test
	public void testGetAllPermissions_NOK() {

		int listSize = 0;
		List<Permission> permissionList = PermissionFactoryForTests.getPermissionList(listSize);
		Mockito.when(permissionDaoMock.getAllPermissions(dbConnectionMock)).thenReturn(permissionList);

		List<Permission> allPermissions = permissionBusiness.getAllPermissions();

		assertNotNull(allPermissions);
		assertEquals(listSize, allPermissions.size());
	}

	@Test
	public void testGetAllPermissionsOfAGivenRole_OK() {

		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);

		String roleName = role.getName();
		Mockito.when(permissionDaoMock.getAllPermissionsOfAGivenRole(dbConnectionMock, roleName)).thenReturn(role.getPermissions());

		List<Permission> rolePermissions = permissionBusiness.getAllPermissionsOfAGivenRole(roleName);

		assertNotNull(rolePermissions);
		assertEquals(listSize, rolePermissions.size());
	}

	@Test
	public void testGetAllPermissionsOfAGivenRole_NOK() {

		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);

		String roleName = role.getName();
		Mockito.when(permissionDaoMock.getAllPermissionsOfAGivenRole(dbConnectionMock, roleName)).thenReturn(role.getPermissions());

		List<Permission> rolePermissions = permissionBusiness.getAllPermissionsOfAGivenRole(roleName);

		assertNotNull(rolePermissions);
		assertEquals(listSize, rolePermissions.size());
	}

	@Test
	public void testDeletePermission_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		int updatedRows = 1;
		long permissionId = permission.getId();
		Mockito.when(permissionDaoMock.deletePermission(dbConnectionMock, permissionId)).thenReturn(updatedRows);

		boolean permissionWasDeleted = permissionBusiness.deletePermission(permissionId);
		assertEquals(true, permissionWasDeleted);
	}

	@Test
	public void testDeletePermission_NOK() {

		int updatedRows = 0;
		long fakePermissionId = -1;
		Mockito.when(permissionDaoMock.deletePermission(dbConnectionMock, fakePermissionId)).thenReturn(updatedRows);

		boolean permissionWasDeleted = permissionBusiness.deletePermission(fakePermissionId);
		assertEquals(false, permissionWasDeleted);
	}
}