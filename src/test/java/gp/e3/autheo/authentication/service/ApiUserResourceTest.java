package gp.e3.autheo.authentication.service;

import static org.junit.Assert.*;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import gp.e3.autheo.authentication.domain.business.ApiUserBusiness;
import gp.e3.autheo.authentication.domain.entities.ApiUser;
import gp.e3.autheo.authentication.infrastructure.exceptions.CheckedIllegalArgumentException;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.authentication.service.resources.ApiUserResource;
import gp.e3.autheo.util.ApiUserFactoryForTests;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.yammer.dropwizard.testing.ResourceTest;

public class ApiUserResourceTest extends ResourceTest {

	private ApiUserBusiness apiUserBusinessMock;
	private ApiUserResource apiUserResource;

	@Override
	protected void setUpResources() throws Exception {

		apiUserBusinessMock = Mockito.mock(ApiUserBusiness.class);
		apiUserResource = new ApiUserResource(apiUserBusinessMock);
		addResource(apiUserResource);
	}

	private Builder getDefaultHttpClient(String url) {

		return client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
	}

	@Test
	public void testCreateApiUser_OK() {		

		try {

			String url = "/api-users";
			ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
			Mockito.when(apiUserBusinessMock.createApiUser(apiUser)).thenReturn(apiUser);
			ClientResponse httpResponse = getDefaultHttpClient(url).post(ClientResponse.class, apiUser);

			assertEquals(201, httpResponse.getStatus());

			ApiUser apiUserFromServer = httpResponse.getEntity(ApiUser.class);
			assertEquals(0, apiUser.compareTo(apiUserFromServer));

		} catch (DuplicateIdException | CheckedIllegalArgumentException e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testCreateApiUser_NOK() {

		try {

			String url = "/api-users";
			ApiUser apiUser = null;
			Mockito.when(apiUserBusinessMock.createApiUser(apiUser)).thenReturn(apiUser);
			ClientResponse httpResponse = getDefaultHttpClient(url).post(ClientResponse.class, apiUser);

			assertEquals(400, httpResponse.getStatus());

		} catch (DuplicateIdException | CheckedIllegalArgumentException e) {

			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testUpdateApiUser_OK() {

		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		String username = apiUser.getUsername();
		String url = "/api-users/" + username;
		ClientResponse httpResponse = getDefaultHttpClient(url).put(ClientResponse.class, apiUser);

		assertEquals(200, httpResponse.getStatus());
	}
	
	@Test
	public void testUpdateApiUser_NOK() {

		ApiUser apiUser = null;
		String username = "anyUsername";
		String url = "/api-users/" + username;
		ClientResponse httpResponse = getDefaultHttpClient(url).put(ClientResponse.class, apiUser);

		assertEquals(400, httpResponse.getStatus());
	}
	
	@Test
	public void testDeleteApiUser_OK() {
		
		ApiUser apiUser = ApiUserFactoryForTests.getDefaultTestApiUser();
		String username = apiUser.getUsername();
		String url = "/api-users/" + username;
		ClientResponse httpResponse = getDefaultHttpClient(url).delete(ClientResponse.class);

		assertEquals(200, httpResponse.getStatus());
	}
	
	@Test
	public void testDeleteApiUser_NOK() {
		
		String emptyUsername = "";
		String url = "/api-users/" + emptyUsername;
		ClientResponse httpResponse = getDefaultHttpClient(url).delete(ClientResponse.class);

		assertEquals(405, httpResponse.getStatus());
	}
}