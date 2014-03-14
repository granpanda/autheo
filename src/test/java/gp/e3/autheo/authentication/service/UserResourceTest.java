package gp.e3.autheo.authentication.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.UserBusiness;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.authentication.service.resources.UserResource;
import gp.e3.autheo.util.UserFactoryForTests;

import java.util.List;

import javax.security.sasl.AuthenticationException;
import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.yammer.dropwizard.testing.ResourceTest;

public class UserResourceTest extends ResourceTest {

	private UserBusiness userBusinessMock = Mockito.mock(UserBusiness.class);
	private TokenBusiness tokenBusinessMock = Mockito.mock(TokenBusiness.class);

	private UserResource userResource;

	@Override
	protected void setUpResources() throws Exception {

		
		
		userResource = new UserResource(userBusinessMock, tokenBusinessMock);
		addResource(userResource);
	}

	//	@Before
	//	public void setUp() {
	//		
	//		userBusinessMock = Mockito.mock(UserBusiness.class);
	//		tokenBusinessMock = Mockito.mock(TokenBusiness.class);
	//		userResource = new UserResource(userBusinessMock, tokenBusinessMock);
	//	}
	//
	//	@After
	//	public void tearDown() {
	//		
	//		userBusinessMock = null;
	//		tokenBusinessMock = null;
	//		userResource = null;
	//	}

	@Test
	public void testCreateUser_OK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			Mockito.when(userBusinessMock.createUser(user)).thenReturn(user);

			String url = "/users";
			ClientResponse httpResponse = client().resource(url)
					.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, user);

			assertEquals(201, httpResponse.getStatus());

			User userFromResponse = httpResponse.getEntity(User.class);
			assertEquals(0, user.compareTo(userFromResponse));

		} catch (DuplicateIdException e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateUser_NOK() {

		try {

			User user = null;

			Mockito.when(userBusinessMock.createUser(user)).thenReturn(user);

			String url = "/users";
			ClientResponse httpResponse = client().resource(url)
					.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, user);

			assertEquals(400, httpResponse.getStatus());

		} catch (DuplicateIdException e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testAuthenticateUser_OK() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String tokenValue = "Hello!123";
			Token testingToken = new Token(user.getUsername(), tokenValue);

			Mockito.when(userBusinessMock.authenticateUser(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
			Mockito.when(tokenBusinessMock.generateToken((User) Mockito.any())).thenReturn(testingToken);

			String url = "/users/token";
			ClientResponse httpResponse = client().resource(url)
					.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, user);

			assertEquals(201, httpResponse.getStatus());

			String generatedToken = httpResponse.getEntity(String.class);

			assertNotNull(generatedToken);
			assertNotEquals(0, generatedToken.length());
			assertEquals(testingToken.getTokenValue(), generatedToken);

		} catch (AuthenticationException | IllegalArgumentException | TokenGenerationException e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAuthenticateUser_NOK_1() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String tokenValue = "Hello!123";
			Token testingToken = new Token(user.getUsername(), tokenValue);

			// The user is not authenticated.
			Mockito.when(userBusinessMock.authenticateUser(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
			Mockito.when(tokenBusinessMock.generateToken((User) Mockito.any())).thenReturn(testingToken);

			String url = "/users/token";
			ClientResponse httpResponse = client().resource(url)
					.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, user);

			assertEquals(401, httpResponse.getStatus());

		} catch (AuthenticationException | IllegalArgumentException | TokenGenerationException e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAuthenticateUser_NOK_2() {

		try {

			User user = UserFactoryForTests.getDefaultTestUser();

			String tokenValue = "Hello!123";
			Token testingToken = new Token(user.getUsername(), tokenValue);

			// The user is not authenticated.
			Mockito.when(userBusinessMock.authenticateUser(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
			Mockito.when(tokenBusinessMock.generateToken((User) Mockito.any())).thenReturn(testingToken);

			String url = "/users/token";
			ClientResponse httpResponse = client().resource(url)
					.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, null); // Send a null user.

			assertEquals(400, httpResponse.getStatus());

		} catch (AuthenticationException | IllegalArgumentException | TokenGenerationException e) {

			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetUserByUsername_OK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		
		Mockito.when(userBusinessMock.getUserByUsername(user.getUsername())).thenReturn(user);
		
		String url = "/users/" + user.getUsername();
		
		ClientResponse httpResponse = client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		
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
		
		ClientResponse httpResponse = client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		
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
		
		ClientResponse httpResponse = client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		
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
		
		ClientResponse httpResponse = client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		
		assertEquals(200, httpResponse.getStatus());
		
		List retrievedUserList = httpResponse.getEntity(List.class);
		
		assertNotNull(retrievedUserList);
		assertEquals(listSize, retrievedUserList.size());
	}
	
	@Test
	public void testGetAllUsers_NOK() {
		
		int listSize = 0;
		List<User> userList = UserFactoryForTests.getUserList(listSize);
		Mockito.when(userBusinessMock.getAllUsers()).thenReturn(userList);
		
		String url = "/users";
		
		ClientResponse httpResponse = client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		
		assertEquals(200, httpResponse.getStatus());
		
		List retrievedUserList = httpResponse.getEntity(List.class);
		
		assertNotNull(retrievedUserList);
		assertEquals(listSize, retrievedUserList.size());
	}
	
	@Test
	public void testUpdateUser_OK() {
		
		User oldUser = UserFactoryForTests.getDefaultTestUser();
		User updatedUser = UserFactoryForTests.getDefaultTestUser(1);
		
		Mockito.doNothing().when(userBusinessMock).updateUser(oldUser.getUsername(), updatedUser);
		
		String url = "/users/" + oldUser.getUsername();
		
		ClientResponse httpResponse = client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, updatedUser);
		
		assertEquals(200, httpResponse.getStatus());
	}
	
	@Test
	public void testUpdateUser_NOK() {
		
		User oldUser = UserFactoryForTests.getDefaultTestUser();
		User updatedUser = null;
		
		Mockito.doNothing().when(userBusinessMock).updateUser(oldUser.getUsername(), updatedUser);
		
		String url = "/users/" + oldUser.getUsername();
		
		ClientResponse httpResponse = client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, updatedUser);
		
		assertEquals(400, httpResponse.getStatus());
	}
	
	@Test
	public void testDeleteUser_OK() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		
		Mockito.doNothing().when(userBusinessMock).deleteUser(user.getUsername());
		
		String url = "/users/" + user.getUsername();
		
		ClientResponse httpResponse = client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);
		
		assertEquals(200, httpResponse.getStatus());
	}
	
	@Test
	public void testDeleteUser_NOK_1() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		
		Mockito.doNothing().when(userBusinessMock).deleteUser(user.getUsername());
		
		String emptyUsername = "";
		String url = "/users/" + emptyUsername;
		
		ClientResponse httpResponse = client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);
		
		/*
		 * The status code should be 405 because we are making a DELETE request to /users/"",
		 * that url gets translated to /users/ so we are trying to execute the DELETE verb over /users/
		 * and that url does not support the DELETE operation.
		 */
		assertEquals(405, httpResponse.getStatus());
	}
	
	@Test
	public void testDeleteUser_NOK_2() {
		
		User user = UserFactoryForTests.getDefaultTestUser();
		
		Mockito.doNothing().when(userBusinessMock).deleteUser(user.getUsername());
		
		String nullUsername = null;
		String url = "/users/" + nullUsername;
		
		ClientResponse httpResponse = client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);
		
		/*
		 * The status code should be 200 because we are making a DELETE request to /users/null,
		 * and even if the user does not exist because DELETE is an idempotent operation.
		 */
		assertEquals(200, httpResponse.getStatus());
	}
}