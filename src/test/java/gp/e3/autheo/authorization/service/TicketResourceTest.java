package gp.e3.autheo.authorization.service;

import static org.junit.Assert.*;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import gp.e3.autheo.authorization.domain.business.TicketBusiness;
import gp.e3.autheo.authorization.domain.entities.Ticket;
import gp.e3.autheo.util.TicketFactoryForTests;

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
	public void testIsAuthorized_OK() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		
		Mockito.when(ticketBusinessMock.tokenWasIssuedByUs((Ticket) Mockito.any())).thenReturn(true);
		Mockito.when(ticketBusinessMock.userIsAuthorized((Ticket) Mockito.any())).thenReturn(true);
		
		String url = "/auth";
		ClientResponse response = getDefaultHttpRequest(url).put(ClientResponse.class, ticket);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void testIsAuthorized_NOK_1() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		
		Mockito.when(ticketBusinessMock.tokenWasIssuedByUs((Ticket) Mockito.any())).thenReturn(true);
		Mockito.when(ticketBusinessMock.userIsAuthorized((Ticket) Mockito.any())).thenReturn(false);
		
		String url = "/auth";
		ClientResponse response = getDefaultHttpRequest(url).put(ClientResponse.class, ticket);
		
		assertEquals(403, response.getStatus());
	}
	
	@Test
	public void testIsAuthorized_NOK_2() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		
		Mockito.when(ticketBusinessMock.tokenWasIssuedByUs((Ticket) Mockito.any())).thenReturn(false);
		
		String url = "/auth";
		ClientResponse response = getDefaultHttpRequest(url).put(ClientResponse.class, ticket);
		
		assertEquals(401, response.getStatus());
	}
}