package gp.e3.autheo.authentication.service;

import static org.junit.Assert.*;
import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.service.resources.OrganizationResource;
import gp.e3.autheo.util.TokenFactoryForTests;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.yammer.dropwizard.testing.ResourceTest;

public class OrganizationResourceTest extends ResourceTest {

	private TokenBusiness tokenBusinessMock;
	private OrganizationResource organizationResource;
	
	@Override
	protected void setUpResources() throws Exception {
		
		tokenBusinessMock = Mockito.mock(TokenBusiness.class);
		organizationResource = new OrganizationResource(tokenBusinessMock);
		addResource(organizationResource);
	}
	
	private Builder getDefaultHttpRequest(String url) {
		return client().resource(url).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void testGetModuleTokenByUserOrganization_OK() {
		
		Token expectedToken = TokenFactoryForTests.getDefaultTestToken();
		String userOrganization = expectedToken.getUserOrganization();
		
		Mockito.when(tokenBusinessMock.getModuleToken(Mockito.anyString())).thenReturn(expectedToken);
		
		String url = "/organizations/" + userOrganization + "/module-token";
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		int responseStatusCode = httpResponse.getStatus();
		
		assertEquals(200, responseStatusCode);
		
		Token token = httpResponse.getEntity(Token.class);
		assertNotNull(token);
		assertEquals(userOrganization, token.getUserOrganization());
	}
	
	@Test
	public void testGetModuleTokenByUserOrganization_NOK_1() {
		
		Token expectedToken = TokenFactoryForTests.getDefaultTestToken();
		String userOrganization = expectedToken.getUserOrganization();
		
		Mockito.when(tokenBusinessMock.getModuleToken(Mockito.anyString())).thenReturn(null);
		
		String url = "/organizations/" + userOrganization + "/module-token";
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		int responseStatusCode = httpResponse.getStatus();
		
		assertEquals(404, responseStatusCode);
		
		String errorMessage = httpResponse.getEntity(String.class);
		assertEquals(true, !StringUtils.isBlank(errorMessage));
	}
	
	@Test
	public void testGetModuleTokenByUserOrganization_NOK_2() {
		
		Token expectedToken = TokenFactoryForTests.getDefaultTestToken();
		String emptyUserOrganization = "";
		
		Mockito.when(tokenBusinessMock.getModuleToken(Mockito.anyString())).thenReturn(expectedToken);
		
		String url = "/organizations/" + emptyUserOrganization + "/module-token";
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		int responseStatusCode = httpResponse.getStatus();
		
		/* 
		 * It does not found the route "GET /organizations//module-token".
		 * Jersey parses as other route. Not to /organizations//module-token with an empty organizationId as expected, 
		 * that's why the expected status code is 404. 
		 */
		assertEquals(404, responseStatusCode);
	}
	
	@Test
	public void testGetModuleTokenByUserOrganization_NOK_3() {
		
		String nullUserOrganization = null;
		// A null string is parsed as "null".
		Mockito.when(tokenBusinessMock.getModuleToken(Mockito.anyString())).thenReturn(null);
		
		String url = "/organizations/" + nullUserOrganization + "/module-token";
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		int responseStatusCode = httpResponse.getStatus();
		
		assertEquals(404, responseStatusCode);
		
		String errorMessage = httpResponse.getEntity(String.class);
		assertEquals(true, !StringUtils.isBlank(errorMessage));
	}
}