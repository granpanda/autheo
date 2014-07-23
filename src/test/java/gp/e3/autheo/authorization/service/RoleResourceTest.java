package gp.e3.autheo.authorization.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authorization.domain.business.RoleBusiness;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.util.RoleFactoryForTests;
import gp.e3.autheo.util.UserFactoryForTests;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.yammer.dropwizard.testing.ResourceTest;

public class RoleResourceTest extends ResourceTest {

	private RoleBusiness roleBusinessMock;
	private RoleResource roleResource;

	@Override
	protected void setUpResources() throws Exception {

		roleBusinessMock = Mockito.mock(RoleBusiness.class);
		roleResource = new RoleResource(roleBusinessMock);
		addResource(roleResource);
	}
	
	private Builder getDefaultHttpRequest(String url) {

		return client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
	}

	@Test
	public void testCreateRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		Mockito.when(roleBusinessMock.createRole((Role) Mockito.any())).thenReturn(role);

		String url = "/roles";
		ClientResponse response = getDefaultHttpRequest(url).post(ClientResponse.class, role);

		assertEquals(201, response.getStatus());
		assertEquals(role.getName(), response.getEntity(Role.class).getName());
	}

	@Test
	public void testCreateRole_NOK_1() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		Mockito.when(roleBusinessMock.createRole((Role) Mockito.any())).thenReturn(null);

		String url = "/roles";
		ClientResponse response = getDefaultHttpRequest(url).post(ClientResponse.class, role);

		assertEquals(500, response.getStatus());
		
		String errorMessage = response.getEntity(String.class);
		assertEquals(false, StringUtils.isBlank(errorMessage));
	}

	@Test
	public void testCreateRole_NOK_2() {

		Role role = null;
		String url = "/roles";
		ClientResponse response = getDefaultHttpRequest(url).post(ClientResponse.class, role);

		assertEquals(400, response.getStatus());
	}

	@Test
	public void testGetRoleByName_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		Mockito.when(roleBusinessMock.getRoleByName((String) Mockito.any())).thenReturn(role);

		String url = "/roles/" + role.getName();
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);

		assertEquals(200, response.getStatus());
		assertEquals(role.getName(), response.getEntity(Role.class).getName());
	}

	@Test
	public void testGetRoleByName_NOK_1() {

		Role notExistentRole = RoleFactoryForTests.getDefaultTestRole();

		Mockito.when(roleBusinessMock.getRoleByName((String) Mockito.any())).thenReturn(null);

		String url = "/roles/" + notExistentRole.getName();
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);

		assertEquals(200, response.getStatus());
		assertNull(response.getEntity(Role.class));
	}

	@Test
	public void testGetRoleByName_NOK_2() {

		String invalidRoleName = "";

		String url = "/roles/" + invalidRoleName;
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);

		// Gets 200 because the request goes this way: /roles/"" -> /roles
		assertEquals(200, response.getStatus());
	}

	@Test
	public void testGetAllRolesNames_OK() {

		List<String> roleNamesList = new ArrayList<String>();
		roleNamesList.add("admin");
		roleNamesList.add("tester");

		Mockito.when(roleBusinessMock.getAllRolesNames()).thenReturn(roleNamesList);

		String url = "/roles";
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);

		assertEquals(200, response.getStatus());
		assertEquals(roleNamesList.size(), response.getEntity(List.class).size());
	}

	@Test
	public void testGetAllRolesNames_NOK() {

		Mockito.when(roleBusinessMock.getAllRolesNames()).thenReturn(null);

		String url = "/roles";
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);

		assertEquals(200, response.getStatus());
		assertNull(response.getEntity(List.class));
	}

	@Test
	public void testUpdateRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		Role updatedRole = RoleFactoryForTests.getDefaultTestRole(3);

		String url = "/roles/" + role.getName();
		ClientResponse response = getDefaultHttpRequest(url).put(ClientResponse.class, updatedRole);

		assertEquals(200, response.getStatus());
	}

	@Test
	public void testUpdateRole_NOK_1() {

		Role updatedRole = RoleFactoryForTests.getDefaultTestRole(3);

		String emptyRoleName = "";
		String url = "/roles/" + emptyRoleName;
		ClientResponse response = getDefaultHttpRequest(url).put(ClientResponse.class, updatedRole);

		// The operation PUT is not supported on /roles
		assertEquals(405, response.getStatus());
	}

	@Test
	public void testUpdateRole_NOK_2() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		Role updatedRole = null;

		String url = "/roles/" + role.getName();
		ClientResponse response = getDefaultHttpRequest(url).put(ClientResponse.class, updatedRole);

		assertEquals(400, response.getStatus());
	}

	@Test
	public void testDeleteRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();

		String url = "/roles/" + role.getName();
		ClientResponse response = getDefaultHttpRequest(url).delete(ClientResponse.class);

		assertEquals(200, response.getStatus());
	}

	@Test
	public void testDeleteRole_NOK() {

		String emptyRoleName = "";
		String url = "/roles/" + emptyRoleName;

		ClientResponse response = getDefaultHttpRequest(url).delete(ClientResponse.class);

		// The operation DELETE is not supported on /roles
		assertEquals(405, response.getStatus());
	}

	@Test
	public void testAddUserToRole_OK() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		User user = UserFactoryForTests.getDefaultTestUser();

		String url = "/roles/" + role.getName() + "/users";
		ClientResponse response = getDefaultHttpRequest(url).post(ClientResponse.class, user);
		
		assertEquals(201, response.getStatus());
	}
	
	@Test
	public void testAddUserToRole_NOK_1() {

		User user = UserFactoryForTests.getDefaultTestUser();

		String emptyUsername = "";
		String url = "/roles/" + emptyUsername + "/users";
		ClientResponse response = getDefaultHttpRequest(url).post(ClientResponse.class, user);
		
		// Does not found the resource which match the route: /roles//users
		assertEquals(404, response.getStatus());
	}
	
	@Test
	public void testAddUserToRole_NOK_2() {

		Role role = RoleFactoryForTests.getDefaultTestRole();
		User user = null;

		String url = "/roles/" + role.getName() + "/users";
		ClientResponse response = getDefaultHttpRequest(url).post(ClientResponse.class, user);
		
		assertEquals(400, response.getStatus());
	}
	
	@Test
	public void testRemoveUserFromRole_OK() {
		
		Role role = RoleFactoryForTests.getDefaultTestRole();
		User user = UserFactoryForTests.getDefaultTestUser();
		
		String url = "/roles/" + role.getName() + "/users/" + user.getUsername();
		ClientResponse response = getDefaultHttpRequest(url).delete(ClientResponse.class);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void testRemoveUserFromRole_NOK_1() {
		
		Role role = RoleFactoryForTests.getDefaultTestRole();
		String emptyUsername = "";
		
		String url = "/roles/" + role.getName() + "/users/" + emptyUsername;
		ClientResponse response = getDefaultHttpRequest(url).delete(ClientResponse.class);
		
		// DELETE is not supported on /roles/roleName/users
		assertEquals(405, response.getStatus());
	}
	
	@Test
	public void testRemoveUserFromRole_NOK_2() {
		
		Role role = RoleFactoryForTests.getDefaultTestRole();
		String nullUsername = null;
		
		String url = "/roles/" + role.getName() + "/users/" + nullUsername;
		ClientResponse response = getDefaultHttpRequest(url).delete(ClientResponse.class);
		
		// The username is parsed as "null" 
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void testGetAllPermissionsOfAGivenRole_OK() {
		
		int numberOfPermissions = 5;
		Role role = RoleFactoryForTests.getDefaultTestRole(numberOfPermissions);
		Mockito.when(roleBusinessMock.getAllPermissionsOfAGivenRole(role.getName())).thenReturn(role.getPermissions());
		
		String url = "/roles/" + role.getName() + "/permissions";
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, response.getStatus());
		assertEquals(numberOfPermissions, role.getPermissions().size());
		assertEquals(role.getPermissions().size(), response.getEntity(List.class).size());
	}
	
	@Test
	public void testGetAllPermissionsOfAGivenRole_NOK() {
		
		int numberOfPermissions = 0;
		Role role = RoleFactoryForTests.getDefaultTestRole(numberOfPermissions);
		Mockito.when(roleBusinessMock.getAllPermissionsOfAGivenRole(role.getName())).thenReturn(role.getPermissions());
		
		String url = "/roles/" + role.getName() + "/permissions";
		ClientResponse response = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, response.getStatus());
		assertEquals(numberOfPermissions, role.getPermissions().size());
		assertEquals(role.getPermissions().size(), response.getEntity(List.class).size());
	}
}