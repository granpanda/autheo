package gp.e3.autheo.authorization.domain.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.authorization.persistence.daos.IPermissionDAO;
import gp.e3.autheo.util.PermissionFactoryForTests;
import gp.e3.autheo.util.RoleFactoryForTests;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PermissionBusinessTest {

	private IPermissionDAO permissionDaoMock;
	private PermissionBusiness permissionBusiness;

	@Before
	public void setUp() {

		permissionDaoMock = Mockito.mock(IPermissionDAO.class);
		permissionBusiness = new PermissionBusiness(permissionDaoMock);
	}

	@After
	public void tearDown() {

		permissionDaoMock = null;
		permissionBusiness = null;
	}

	@Test
	public void testCreatePermission_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			Permission createdPermission = permissionBusiness.createPermission(permission);
			assertEquals(0, permission.compareTo(createdPermission));

		} catch (DuplicateIdException e) {

			fail("The method should not throw an exception");
		}
	}

	@Test
	public void testCreatePermission_NOK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			String errorMessage = "The permission was already created into the db.";

			Mockito.doNothing().doThrow(new DuplicateIdException(errorMessage)).when(permissionDaoMock)
			.createPermission(permission.getName(), permission.getHttpVerb(), permission.getUrl());

			Permission createdPermission = permissionBusiness.createPermission(permission);
			assertEquals(0, permission.compareTo(createdPermission));

			permissionBusiness.createPermission(permission);

			fail("The method should throw an exception");

		} catch (DuplicateIdException e) {

			assertNotNull(e);

		} catch (Exception e) {

			assertNotNull(e);
		}
	}

	@Test
	public void testGetPermissionById_OK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			Permission createdPermission = permissionBusiness.createPermission(permission);
			assertEquals(0, permission.compareTo(createdPermission));

			Mockito.when(permissionDaoMock.getPermissionById(permission.getId())).thenReturn(permission);
			
			Permission retrievedPermission = permissionBusiness.getPermissionById(permission.getId());
			assertEquals(0, permission.compareTo(retrievedPermission));

		} catch (DuplicateIdException e) {

			fail("The method should not throw an exception");
		}
	}
	
	@Test
	public void testGetPermissionById_NOK() {

		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			Permission createdPermission = permissionBusiness.createPermission(permission);
			assertEquals(0, permission.compareTo(createdPermission));

			int fakePermissionId = -1;
			Mockito.when(permissionDaoMock.getPermissionById(fakePermissionId)).thenReturn(null);
			
			Permission retrievedPermission = permissionBusiness.getPermissionById(permission.getId());
			assertNull(retrievedPermission);

		} catch (DuplicateIdException e) {

			fail("The method should not throw an exception");
		}
	}
	
	@Test
	public void testGetPermissionByHttpVerbAndUrl_OK() {
		
		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			Permission createdPermission = permissionBusiness.createPermission(permission);
			assertEquals(0, permission.compareTo(createdPermission));

			Mockito.when(permissionDaoMock.getPermissionByHttpVerbAndUrl(permission.getHttpVerb(), permission.getUrl()))
				.thenReturn(permission);
			
			Permission retrievedPermission = permissionBusiness.getPermissionByHttpVerbAndUrl(permission.getHttpVerb(), 
					permission.getUrl());
			
			assertEquals(0, permission.compareTo(retrievedPermission));

		} catch (DuplicateIdException e) {

			fail("The method should not throw an exception");
		}
	}
	
	@Test
	public void testGetPermissionByHttpVerbAndUrl_NOK() {
		
		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();

		try {

			Permission createdPermission = permissionBusiness.createPermission(permission);
			assertEquals(0, permission.compareTo(createdPermission));

			String fakeHttpVerb = "FAKE";
			String fakeUrl = "www.fake.com";
			Mockito.when(permissionDaoMock.getPermissionByHttpVerbAndUrl(fakeHttpVerb, fakeUrl)).thenReturn(null);
			
			Permission retrievedPermission = permissionBusiness.getPermissionByHttpVerbAndUrl(fakeHttpVerb, fakeUrl);
			
			assertNull(retrievedPermission);

		} catch (DuplicateIdException e) {

			fail("The method should not throw an exception");
		}
	}
	
	@Test
	public void testGetAllPermissions_OK() {
		
		int listSize = 5;
		List<Permission> permissionList = PermissionFactoryForTests.getPermissionList(listSize);
		Mockito.when(permissionDaoMock.getAllPermissions()).thenReturn(permissionList);
		
		List<Permission> allPermissions = permissionBusiness.getAllPermissions();
		
		assertNotNull(allPermissions);
		assertEquals(listSize, allPermissions.size());
	}
	
	@Test
	public void testGetAllPermissions_NOK() {
		
		int listSize = 0;
		List<Permission> permissionList = PermissionFactoryForTests.getPermissionList(listSize);
		Mockito.when(permissionDaoMock.getAllPermissions()).thenReturn(permissionList);
		
		List<Permission> allPermissions = permissionBusiness.getAllPermissions();
		
		assertNotNull(allPermissions);
		assertEquals(listSize, allPermissions.size());
	}
	
	@Test
	public void testGetAllPermissionsOfAGivenRole_OK() {
		
		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);
		
		String roleName = role.getName();
		Mockito.when(permissionDaoMock.getAllPermissionsOfAGivenRole(roleName)).thenReturn(role.getPermissions());
		
		List<Permission> rolePermissions = permissionBusiness.getAllPermissionsOfAGivenRole(roleName);
		
		assertNotNull(rolePermissions);
		assertEquals(listSize, rolePermissions.size());
	}
	
	@Test
	public void testGetAllPermissionsOfAGivenRole_NOK() {
		
		int listSize = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(listSize);
		
		String roleName = role.getName();
		Mockito.when(permissionDaoMock.getAllPermissionsOfAGivenRole(roleName)).thenReturn(role.getPermissions());
		
		List<Permission> rolePermissions = permissionBusiness.getAllPermissionsOfAGivenRole(roleName);
		
		assertNotNull(rolePermissions);
		assertEquals(listSize, rolePermissions.size());
	}
	
	@Test
	public void testDeletePermission_OK() {
		
		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();
		
		int updatedRows = 1;
		int permissionId = permission.getId();
		Mockito.when(permissionDaoMock.deletePermission(permissionId)).thenReturn(updatedRows);
		
		int answer = permissionBusiness.deletePermission(permissionId);
		
		assertEquals(updatedRows, answer);
	}
	
	@Test
	public void testDeletePermission_NOK() {
		
		int updatedRows = 0;
		int fakePermissionId = -1;
		Mockito.when(permissionDaoMock.deletePermission(fakePermissionId)).thenReturn(updatedRows);
		
		int answer = permissionBusiness.deletePermission(fakePermissionId);
		
		assertEquals(updatedRows, answer);
	}
}