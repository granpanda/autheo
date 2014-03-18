package gp.e3.autheo.authorization.service;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.authorization.domain.business.PermissionBusiness;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.util.PermissionFactoryForTests;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.yammer.dropwizard.testing.ResourceTest;

public class PermissionResourceTest extends ResourceTest {

	private PermissionBusiness permissionBusinessMock;
	private PermissionResource permissionResource;
	
	@Override
	protected void setUpResources() throws Exception {
		
		permissionBusinessMock = Mockito.mock(PermissionBusiness.class);
		permissionResource = new PermissionResource(permissionBusinessMock);
		addResource(permissionResource);
	}
	
	private Builder getDefaultHttpRequest(String url) {

		return client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void testCreatePermission_OK() {
		
		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();
		
		try {
			Mockito.when(permissionBusinessMock.createPermission((Permission) Mockito.any())).thenReturn(permission);
		} catch (DuplicateIdException e) {
			fail(e.getMessage());
		}
		
		String url = "/permissions";
		ClientResponse response = getDefaultHttpRequest(url).post(ClientResponse.class, permission);
		
		assertEquals(201, response.getStatus());
		assertEquals(0, permission.compareTo(response.getEntity(Permission.class)));
	}
	
	@Test
	public void testCreatePermission_NOK_1() {
		
		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();
		
		try {
			
			String errorMessage = "The given permission already exists into the db.";
			
			Mockito.when(permissionBusinessMock.createPermission((Permission) Mockito.any())).thenReturn(permission)
			.thenThrow(new DuplicateIdException(errorMessage));
			
		} catch (DuplicateIdException e) {
			fail(e.getMessage());
		}
		
		String url = "/permissions";
		ClientResponse response = getDefaultHttpRequest(url).post(ClientResponse.class, permission);
		
		assertEquals(201, response.getStatus());
		assertEquals(0, permission.compareTo(response.getEntity(Permission.class)));
		
		ClientResponse secondResponse = getDefaultHttpRequest(url).post(ClientResponse.class, permission);
		
		assertEquals(500, secondResponse.getStatus());
	}
	
	@Test
	public void testCreatePermission_NOK_2() {
		
		Permission permission = null;
		
		String url = "/permissions";
		ClientResponse response = getDefaultHttpRequest(url).post(ClientResponse.class, permission);
		
		assertEquals(400, response.getStatus());
	}
	
	@Test
	public void testGetPermissionById_OK() {
		
		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();
		
		Mockito.when(permissionBusinessMock.getPermissionById(permission.getId())).thenReturn(permission);
		
		String url = "/permissions/" + permission.getId();
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, response.getStatus());
		assertEquals(0, permission.compareTo(response.getEntity(Permission.class)));
	}
	
	@Test
	public void testGetPermissionById_NOK_1() {
		
		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();
		
		Mockito.when(permissionBusinessMock.getPermissionById(permission.getId())).thenReturn(null);
		
		String url = "/permissions/" + permission.getId();
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, response.getStatus());
		assertNull(response.getEntity(Permission.class));
	}
	
	@Test
	public void testGetPermissionById_NOK_2() {
		
		String invalidPermissionId = "qwe123";
		
		String url = "/permissions/" + invalidPermissionId;
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(400, response.getStatus());
	}
	
	@Test
	public void testGetAllPermissions_OK() {
		
		int listSize = 5;
		List<Permission> permissionList = PermissionFactoryForTests.getPermissionList(listSize);
		
		Mockito.when(permissionBusinessMock.getAllPermissions()).thenReturn(permissionList);
		
		String url = "/permissions";
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, response.getStatus());
		assertEquals(listSize, permissionList.size());
		
		List<Permission> retrievedPermissions = response.getEntity(List.class);
		assertEquals(permissionList.size(), retrievedPermissions.size());
	}
	
	@Test
	public void testGetAllPermissions_NOK() {
		
		int listSize = 0;
		List<Permission> permissionList = PermissionFactoryForTests.getPermissionList(listSize);
		
		Mockito.when(permissionBusinessMock.getAllPermissions()).thenReturn(permissionList);
		
		String url = "/permissions";
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, response.getStatus());
		assertEquals(listSize, permissionList.size());
		
		List<Permission> retrievedPermissions = response.getEntity(List.class);
		assertEquals(permissionList.size(), retrievedPermissions.size());
	}
	
	@Test
	public void testDeletePermission_OK() {
		
		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();
		
		String url = "/permissions/" + permission.getId();
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void testDeletePermission_NOK() {
		
		String invalidPermissionId = "qwe123";
		String url = "/permissions/" + invalidPermissionId;
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(400, response.getStatus());
	}
}