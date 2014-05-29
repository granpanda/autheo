package gp.e3.autheo.authentication.service;

import static org.junit.Assert.*;

import java.sql.SQLException;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.service.resources.TokenResource;

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
	public void testUpdateTokensCache_OK() {
		
		try {
			
			Mockito.doNothing().when(tokenBusinessMock).updateTokensCache();
			
			String url = "/tokens";
			ClientResponse httpResponse = getDefaultHttpRequest(url).put(ClientResponse.class);
			assertEquals(200, httpResponse.getStatus());
			
		} catch (SQLException e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void testUpdateTokensCache_NOK() {
		
		try {
			
			Mockito.doThrow(SQLException.class).when(tokenBusinessMock).updateTokensCache();
			
			String url = "/tokens";
			ClientResponse httpResponse = getDefaultHttpRequest(url).put(ClientResponse.class);
			assertEquals(500, httpResponse.getStatus());
			
		} catch (SQLException e) {
			
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}