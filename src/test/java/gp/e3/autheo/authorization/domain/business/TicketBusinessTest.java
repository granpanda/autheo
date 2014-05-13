package gp.e3.autheo.authorization.domain.business;

import static org.junit.Assert.*;
import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.infrastructure.exceptions.CheckedIllegalArgumentException;
import gp.e3.autheo.authorization.domain.entities.Ticket;
import gp.e3.autheo.authorization.infrastructure.dtos.PermissionTuple;
import gp.e3.autheo.util.TicketFactoryForTests;
import gp.e3.autheo.util.UserFactoryForTests;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TicketBusinessTest {

	private RoleBusiness roleBusinessMock;
	private TokenBusiness tokenBusinessMock;

	private TicketBusiness ticketBusiness;

	@Before
	public void setUp() {

		roleBusinessMock = Mockito.mock(RoleBusiness.class);
		tokenBusinessMock = Mockito.mock(TokenBusiness.class);

		ticketBusiness = new TicketBusiness(tokenBusinessMock, roleBusinessMock);
	}

	@After
	public void tearDown() {

		roleBusinessMock = null;
		tokenBusinessMock = null;

		ticketBusiness = null;
	}

	@Test
	public void testTokenWasIssuedByUs_OK() {

		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();

		User user = UserFactoryForTests.getDefaultTestUser();
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId());

		try {
			
			Mockito.when(tokenBusinessMock.getToken(ticket.getTokenValue())).thenReturn(token);
			
		} catch (CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}

		Token retrievedToken = ticketBusiness.tokenWasIssuedByUs(ticket);
		assertTrue(retrievedToken != null);
	}

	@Test
	public void testTokenWasIssuedByUs_NOK_1() {

		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();

		User user = UserFactoryForTests.getDefaultTestUser();

		String tokenValue = "";

		try {
			tokenValue = TokenFactory.getToken(user);
		} catch (TokenGenerationException | CheckedIllegalArgumentException e) {
			fail(e.getMessage());
		}

		// The token value in the ticket is different than the token value of the token object. 
		Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());

		try {
			
			Mockito.when(tokenBusinessMock.getToken(ticket.getTokenValue())).thenReturn(token);
			
		} catch (CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}

		Token retrievedToken = ticketBusiness.tokenWasIssuedByUs(ticket);
		assertNotNull(retrievedToken);

		// It is also false if the ticket given as argument is null
		retrievedToken = ticketBusiness.tokenWasIssuedByUs(null);
		assertNull(retrievedToken);
	}
	
	@Test
	public void testTokenWasIssuedByUs_NOK_2() {

		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();

		String errorMessage = "The parameter is null or empty.";
		IllegalArgumentException illegalArgumentException = new IllegalArgumentException(errorMessage);
		try {
			
			Mockito.when(tokenBusinessMock.getToken(ticket.getTokenValue())).thenThrow(illegalArgumentException);
			
		} catch (CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}

		Token retrievedToken = ticketBusiness.tokenWasIssuedByUs(ticket);
		assertFalse(retrievedToken != null);
	}

	@Test
	public void testUserIsAuthorized_OK_1() {

		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();

		User user = UserFactoryForTests.getDefaultTestUser();
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId());

		String roleName = token.getUserRole();

		int numberOfPermissions = 3;
		List<PermissionTuple> permissionTuples = new ArrayList<PermissionTuple>();

		for (int i = 0; i < numberOfPermissions; i++) {

			PermissionTuple permissionTuple = new PermissionTuple("GET", "www.google.com" + i);
			permissionTuples.add(permissionTuple);
		}

		// Add the permission requested in the ticket.
		permissionTuples.add(new PermissionTuple(ticket.getHttpVerb(), ticket.getRequestedUrl()));

		try {
			
			Mockito.when(tokenBusinessMock.getToken(ticket.getTokenValue())).thenReturn(token);
			
		} catch (CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
		
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(roleName)).thenReturn(true);
		Mockito.when(roleBusinessMock.getRolePermissionsFromRedis(roleName)).thenReturn(permissionTuples);

		assertTrue(ticketBusiness.userIsAuthorized(ticket));
	}

	@Test
	public void testUserIsAuthorized_OK_2() {

		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();

		User user = UserFactoryForTests.getDefaultTestUser();
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId());

		String roleName = token.getUserRole();

		int numberOfPermissions = 3;
		List<PermissionTuple> permissionTuples = new ArrayList<PermissionTuple>();

		for (int i = 0; i < numberOfPermissions; i++) {

			PermissionTuple permissionTuple = new PermissionTuple("GET", "www.google.com" + i);
			permissionTuples.add(permissionTuple);
		}

		// Add the permission requested in the ticket.
		permissionTuples.add(new PermissionTuple(ticket.getHttpVerb(), ticket.getRequestedUrl()));

		try {
			
			Mockito.when(tokenBusinessMock.getToken(ticket.getTokenValue())).thenReturn(token);
			
		} catch (CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
		
		// First time say return false.
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(roleName)).thenReturn(false);
		Mockito.when(roleBusinessMock.addRolePermissionsToRedis(roleName)).thenReturn(true);
		// Second time return true.
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(roleName)).thenReturn(true);
		Mockito.when(roleBusinessMock.getRolePermissionsFromRedis(roleName)).thenReturn(permissionTuples);

		assertTrue(ticketBusiness.userIsAuthorized(ticket));
	}

	@Test
	public void testUserIsAuthorized_NOK_1() {

		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();

		User user = UserFactoryForTests.getDefaultTestUser();
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId());

		String roleName = token.getUserRole();

		List<PermissionTuple> permissionTuples = new ArrayList<PermissionTuple>();

		try {
			
			Mockito.when(tokenBusinessMock.getToken(ticket.getTokenValue())).thenReturn(token);
			
		} catch (CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
		
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(roleName)).thenReturn(true);

		// Return an empty list of permission tuples.
		Mockito.when(roleBusinessMock.getRolePermissionsFromRedis(roleName)).thenReturn(permissionTuples);

		assertFalse(ticketBusiness.userIsAuthorized(ticket));
	}

	@Test
	public void testUserIsAuthorized_NOK_2() {

		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();

		User user = UserFactoryForTests.getDefaultTestUser();
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId());

		String roleName = token.getUserRole();

		int numberOfPermissions = 3;
		List<PermissionTuple> permissionTuples = new ArrayList<PermissionTuple>();

		for (int i = 0; i < numberOfPermissions; i++) {

			PermissionTuple permissionTuple = new PermissionTuple("GET", "www.google.com" + i);
			permissionTuples.add(permissionTuple);
		}

		// Add the permission requested in the ticket.
		permissionTuples.add(new PermissionTuple(ticket.getHttpVerb(), ticket.getRequestedUrl()));

		try {
			
			Mockito.when(tokenBusinessMock.getToken(ticket.getTokenValue())).thenReturn(token);
			
		} catch (CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
		
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(roleName)).thenReturn(false);
		Mockito.when(roleBusinessMock.addRolePermissionsToRedis(roleName)).thenReturn(false);

		assertFalse(ticketBusiness.userIsAuthorized(ticket));
	}
}