package gp.e3.autheo.authentication.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.UserBusiness;
import gp.e3.autheo.authentication.domain.business.constants.TokenTypes;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.service.resources.UserResource;
import gp.e3.autheo.authorization.domain.business.RoleBusiness;
import gp.e3.autheo.util.UserFactoryForTests;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.yammer.dropwizard.testing.ResourceTest;

public class UserResourceTest extends ResourceTest {

	private UserBusiness userBusinessMock;
	private RoleBusiness roleBusinessMock;
	private TokenBusiness tokenBusinessMock;
	private UserResource userResource;

	@Override
	protected void setUpResources() throws Exception {

		userBusinessMock = Mockito.mock(UserBusiness.class);
		roleBusinessMock = Mockito.mock(RoleBusiness.class);
		tokenBusinessMock = Mockito.mock(TokenBusiness.class);
		
		userResource = new UserResource(userBusinessMock, roleBusinessMock, tokenBusinessMock);
		addResource(userResource);
	}
	
	private Builder getDefaultHttpRequest(String url) {
		return client().resource(url).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
	}

	@Test
	public void testCreateUser_OK() {

		User user = UserFactoryForTests.getDefaultTestUser();

		User expectedCreatedUser = user;
		Mockito.when(userBusinessMock.createUser(Mockito.any(User.class))).thenReturn(expectedCreatedUser);
		Mockito.when(tokenBusinessMock.generateAndSaveTokensForAnAPIUser(Mockito.any(User.class))).thenReturn(true);
		Mockito.when(roleBusinessMock.addUserToRole(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

		String url = "/users";
		ClientResponse httpResponse = getDefaultHttpRequest(url).post(ClientResponse.class, user);

		assertEquals(201, httpResponse.getStatus());

		User createdUser = httpResponse.getEntity(User.class);
		assertEquals(0, expectedCreatedUser.compareTo(createdUser));
	}
	
	@Test
	public void testCreateUser_NOK_1_exception() {
		
		try {
			
			User user = UserFactoryForTests.getDefaultTestUser();

			Mockito.doThrow(IllegalStateException.class).when(userBusinessMock).createUser(Mockito.any(User.class));
			Mockito.when(tokenBusinessMock.generateAndSaveTokensForAnAPIUser(Mockito.any(User.class))).thenReturn(true);
			Mockito.when(roleBusinessMock.addUserToRole(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
			
			String url = "/users";
			ClientResponse httpResponse = getDefaultHttpRequest(url).post(ClientResponse.class, user);

			assertEquals(500, httpResponse.getStatus());
			
		} catch (IllegalStateException e) {
			
			assertNotNull(e);
		}
	}

	@Test
	public void testCreateUser_NOK_2_nullUser() {

		User user = null;
		String url = "/users";
		ClientResponse httpResponse = getDefaultHttpRequest(url).post(ClientResponse.class, user);

		assertEquals(400, httpResponse.getStatus());
	}

	@Test
	public void testAuthenticateUser_OK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String tokenValue = "Hello!123";
			String username = user.getUsername();
			Token testingToken = new Token(tokenValue, username, user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

			Mockito.when(userBusinessMock.authenticateUser(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
			Mockito.when(tokenBusinessMock.generateToken((User) Mockito.any())).thenReturn(testingToken);

			String url = "/users/" + username + "/tokens";
			ClientResponse httpResponse = getDefaultHttpRequest(url).post(ClientResponse.class, user);

			assertEquals(201, httpResponse.getStatus());

			Token generatedToken = httpResponse.getEntity(Token.class);

			assertNotNull(generatedToken);
			assertEquals(0, testingToken.compareTo(generatedToken));

		} catch (IllegalArgumentException | TokenGenerationException e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAuthenticateUser_NOK_1() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String tokenValue = "Hello!123";
			String username = user.getUsername();
			Token testingToken = new Token(tokenValue, username, user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

			// The user is not authenticated.
			Mockito.when(userBusinessMock.authenticateUser(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
			Mockito.when(tokenBusinessMock.generateToken((User) Mockito.any())).thenReturn(testingToken);

			String url = "/users/" + username + "/tokens";
			ClientResponse httpResponse = getDefaultHttpRequest(url).post(ClientResponse.class, user);

			assertEquals(401, httpResponse.getStatus());

		} catch (IllegalArgumentException | TokenGenerationException e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAuthenticateUser_NOK_2() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String tokenValue = "Hello!123";
			String username = user.getUsername();
			Token testingToken = new Token(tokenValue, username, user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

			// The user is not authenticated.
			Mockito.when(userBusinessMock.authenticateUser(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
			Mockito.when(tokenBusinessMock.generateToken((User) Mockito.any())).thenReturn(testingToken);

			String url = "/users/" + username + "/tokens";
			ClientResponse httpResponse = getDefaultHttpRequest(url).post(ClientResponse.class, null); // Send a null user.

			assertEquals(400, httpResponse.getStatus());

		} catch (IllegalArgumentException | TokenGenerationException e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetUserByUsername_OK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		
		Mockito.when(userBusinessMock.getUserByUsername(user.getUsername())).thenReturn(user);
		
		String url = "/users/" + user.getUsername();
		
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, httpResponse.getStatus());
		
		User retrievedUser = httpResponse.getEntity(User.class);
		assertEquals(0, user.compareTo(retrievedUser));
	}
	
	@Test
	public void testGetUserByUsername_NOK_1() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		
		Mockito.when(userBusinessMock.getUserByUsername(user.getUsername())).thenReturn(user);
		
		String emptyUsername = "";
		String url = "/users/" + emptyUsername;
		
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		/* 
		 * The status code should be 200 because we are making the GET request to /users/"", 
		 * which is translated to GET /users/, and this is the reason we get an empty list.
		 */
		assertEquals(200, httpResponse.getStatus());
	}
	
	@Test
	public void testGetUserByUsername_NOK_2() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		
		Mockito.when(userBusinessMock.getUserByUsername(user.getUsername())).thenReturn(user);
		
		String nullUsername = null;
		String url = "/users/" + nullUsername;
		
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		/*
		 * The status code should be 200 because we are making a GET request to /users/null,
		 * and even if the user does not exist because GET is an idempotent operation.
		 */
		assertEquals(200, httpResponse.getStatus());
	}
	
	@Test
	public void testGetAllUsers_OK() {
		
		int listSize = 5;
		List<User> userList = UserFactoryForTests.getUserList(listSize);
		Mockito.when(userBusinessMock.getAllUsers()).thenReturn(userList);
		
		String url = "/users";
		
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, httpResponse.getStatus());
		
		User[] retrievedUserArray = httpResponse.getEntity(User[].class);
		
		assertNotNull(retrievedUserArray);
		assertEquals(listSize, retrievedUserArray.length);
	}
	
	@Test
	public void testGetAllUsers_NOK() {
		
		int listSize = 0;
		List<User> userList = UserFactoryForTests.getUserList(listSize);
		Mockito.when(userBusinessMock.getAllUsers()).thenReturn(userList);
		
		String url = "/users";
		
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, httpResponse.getStatus());
		
		User[] retrievedUserArray = httpResponse.getEntity(User[].class);
		
		assertNotNull(retrievedUserArray);
		assertEquals(listSize, retrievedUserArray.length);
	}
	
	@Test
	public void testUpdateUser_OK() {
		
		User oldUser = UserFactoryForTests.getDefaultTestUser();
		User updatedUser = UserFactoryForTests.getDefaultTestUser(1);
		
		boolean expectedResult = true;
		Mockito.when(userBusinessMock.updateUser(Mockito.anyString(), Mockito.any(User.class))).thenReturn(expectedResult);
		
		String url = "/users/" + oldUser.getUsername();
		ClientResponse httpResponse = getDefaultHttpRequest(url).put(ClientResponse.class, updatedUser);
		assertEquals(200, httpResponse.getStatus());
	}
	
	@Test
	public void testUpdateUser_NOK_1() {
		
		User oldUser = UserFactoryForTests.getDefaultTestUser();
		User updatedUser = UserFactoryForTests.getDefaultTestUser(1);
		
		boolean expectedResult = false;
		Mockito.when(userBusinessMock.updateUser(Mockito.anyString(), Mockito.any(User.class))).thenReturn(expectedResult);
		
		String url = "/users/" + oldUser.getUsername();
		ClientResponse httpResponse = getDefaultHttpRequest(url).put(ClientResponse.class, updatedUser);
		assertEquals(500, httpResponse.getStatus());
	}
	
	@Test
	public void testUpdateUser_NOK_2() {
		
		User oldUser = UserFactoryForTests.getDefaultTestUser();
		User updatedUser = null;
		
		String url = "/users/" + oldUser.getUsername();
		ClientResponse httpResponse = getDefaultHttpRequest(url).put(ClientResponse.class, updatedUser);
		assertEquals(400, httpResponse.getStatus());
	}
	
	@Test
	public void testDeleteUser_OK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		
		boolean expectedResult = true;
		Mockito.when(userBusinessMock.deleteUser(Mockito.anyString())).thenReturn(expectedResult);
		
		String url = "/users/" + user.getUsername();
		ClientResponse httpResponse = getDefaultHttpRequest(url).delete(ClientResponse.class);
		
		assertEquals(200, httpResponse.getStatus());
	}
	
	@Test
	public void testDeleteUser_NOK_1() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		
		boolean expectedResult = false;
		Mockito.when(userBusinessMock.deleteUser(Mockito.anyString())).thenReturn(expectedResult);
		
		String url = "/users/" + user.getUsername();
		ClientResponse httpResponse = getDefaultHttpRequest(url).delete(ClientResponse.class);
		
		assertEquals(500, httpResponse.getStatus());
	}
	
	@Test
	public void testDeleteUser_NOK_2() {
		
		String emptyUsername = "";
		String url = "/users/" + emptyUsername;
		
		ClientResponse httpResponse = getDefaultHttpRequest(url).delete(ClientResponse.class);
		
		/*
		 * The status code should be 405 because we are making a DELETE request to /users/"",
		 * that url gets translated to /users/ so we are trying to execute the DELETE verb over /users/
		 * and that url does not support the DELETE operation.
		 */
		assertEquals(405, httpResponse.getStatus());
	}
	
	@Test
	public void testDeleteUser_NOK_3() {
		
		boolean expectedResult = true;
		Mockito.when(userBusinessMock.deleteUser(Mockito.anyString())).thenReturn(expectedResult);
		
		String nullUsername = null;
		String url = "/users/" + nullUsername;
		
		ClientResponse httpResponse = getDefaultHttpRequest(url).delete(ClientResponse.class);
		
		/*
		 * The status code should be 200 because we are making a DELETE request to /users/null,
		 * and even if the user does not exist because DELETE is an idempotent operation.
		 */
		assertEquals(200, httpResponse.getStatus());
	}
}
