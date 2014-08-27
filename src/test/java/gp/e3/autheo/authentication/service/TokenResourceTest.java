package gp.e3.autheo.authentication.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.exceptions.ValidDataException;
import gp.e3.autheo.authentication.service.resources.TokenResource;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.yammer.dropwizard.testing.ResourceTest;

public class TokenResourceTest extends ResourceTest {
	
	private TokenBusiness tokenBusinessMock;
	private TokenResource tokenResource;

	@Override
	protected void setUpResources() throws Exception {
		
		tokenBusinessMock = Mockito.mock(TokenBusiness.class);
		tokenResource = new TokenResource(tokenBusinessMock);
		addResource(tokenResource);
	}
	
	private Builder getDefaultHttpRequest(String url) {
		return client().resource(url).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void testRemoveUserAccessTokenFromCache_OK1() {
		
		String url = "/tokens/test/cache";
		
		try {
			Mockito.when(tokenBusinessMock.removeUserAccessToken("test")).thenReturn(true);

			ClientResponse httpResponse = getDefaultHttpRequest(url).delete(ClientResponse.class);
			assertEquals(200, httpResponse.getStatus());
			
			Mockito.when(tokenBusinessMock.removeUserAccessToken("test")).thenReturn(false);

			httpResponse = getDefaultHttpRequest(url).delete(ClientResponse.class);
			assertEquals(409, httpResponse.getStatus());
			
		} catch (ValidDataException e) {
			fail("Exception Unexpected: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRemoveUserAccessTokenFromCache_NOK1() {
		
		String url = "/tokens/test/cache";
		
		ClientResponse httpResponse = null;
		
		try {
			
			Mockito.doThrow(ValidDataException.class).when(tokenBusinessMock).removeUserAccessToken(Mockito.anyString());

			httpResponse = getDefaultHttpRequest(url).delete(ClientResponse.class);
			assertEquals(409, httpResponse.getStatus());
			
		} catch (ValidDataException e) {
			fail("Exception Unexpected: " + e.getMessage());
			e.printStackTrace();
		}
	}
}