package gp.e3.autheo.authorization.service;

import static org.junit.Assert.assertEquals;
import gp.e3.autheo.authentication.domain.business.constants.TokenTypes;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authorization.domain.business.TicketBusiness;
import gp.e3.autheo.authorization.domain.entities.Ticket;
import gp.e3.autheo.util.TicketFactoryForTests;
import gp.e3.autheo.util.UserFactoryForTests;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.yammer.dropwizard.testing.ResourceTest;

public class TicketResourceTest extends ResourceTest {

	private TicketBusiness ticketBusinessMock;
	private TicketResource ticketResource;
	
	@Override
	protected void setUpResources() throws Exception {
		
		ticketBusinessMock = Mockito.mock(TicketBusiness.class);
		ticketResource = new TicketResource(ticketBusinessMock);
		addResource(ticketResource);
	}
	
	private Builder getDefaultHttpRequest(String url) {

		return client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void testIsAuthorized_OK_1() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		
		Mockito.when(ticketBusinessMock.isPublicPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		
		String url = "/auth";
		ClientResponse response = getDefaultHttpRequest(url).put(ClientResponse.class, ticket);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void testIsAuthorized_OK_2() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		
		User user = UserFactoryForTests.getDefaultTestUser();
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());
		
		Mockito.when(ticketBusinessMock.isPublicPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
		Mockito.when(ticketBusinessMock.tokenWasIssuedByUs((Ticket) Mockito.any())).thenReturn(token);
		Mockito.when(ticketBusinessMock.userIsAuthorized((Ticket) Mockito.any())).thenReturn(true);
		
		String url = "/auth";
		ClientResponse response = getDefaultHttpRequest(url).put(ClientResponse.class, ticket);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void testIsAuthorized_NOK_1() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		
		User user = UserFactoryForTests.getDefaultTestUser();
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());
		
		Mockito.when(ticketBusinessMock.tokenWasIssuedByUs((Ticket) Mockito.any())).thenReturn(token);
		Mockito.when(ticketBusinessMock.userIsAuthorized((Ticket) Mockito.any())).thenReturn(false);
		
		String url = "/auth";
		ClientResponse response = getDefaultHttpRequest(url).put(ClientResponse.class, ticket);
		
		assertEquals(403, response.getStatus());
	}
	
	@Test
	public void testIsAuthorized_NOK_2() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		
		Mockito.when(ticketBusinessMock.tokenWasIssuedByUs((Ticket) Mockito.any())).thenReturn(null);
		
		String url = "/auth";
		ClientResponse response = getDefaultHttpRequest(url).put(ClientResponse.class, ticket);
		
		assertEquals(401, response.getStatus());
	}
}