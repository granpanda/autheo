package gp.e3.autheo.authorization.domain.business;

import static org.junit.Assert.*;
import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.business.constants.TokenTypes;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authorization.domain.entities.Ticket;
import gp.e3.autheo.authorization.infrastructure.constants.EnumRoleConstants;
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
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

		Mockito.when(tokenBusinessMock.getAPIToken(ticket.getTokenValue())).thenReturn(token);

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
		} catch (IllegalArgumentException | TokenGenerationException e) {
			fail(e.getMessage());
		}

		// The token value in the ticket is different than the token value of the token object. 
		Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

		Mockito.when(tokenBusinessMock.getAPIToken(ticket.getTokenValue())).thenReturn(token);

		Token retrievedToken = ticketBusiness.tokenWasIssuedByUs(ticket);
		assertNotNull(retrievedToken);

		// It is also false if the ticket given as argument is null
		retrievedToken = ticketBusiness.tokenWasIssuedByUs(null);
		assertNull(retrievedToken);
	}

	@Test
	public void testUserIsAuthorized_OK_1() {

		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();

		User user = UserFactoryForTests.getDefaultTestUser();
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

		String roleName = token.getUserRole();

		int numberOfPermissions = 3;
		List<PermissionTuple> permissionTuples = new ArrayList<PermissionTuple>();

		for (int i = 0; i < numberOfPermissions; i++) {

			PermissionTuple permissionTuple = new PermissionTuple("GET", "www.google.com" + i);
			permissionTuples.add(permissionTuple);
		}

		// Add the permission requested in the ticket.
		permissionTuples.add(new PermissionTuple(ticket.getHttpVerb(), ticket.getRequestedUrl()));

		Mockito.when(tokenBusinessMock.getAPIToken(ticket.getTokenValue())).thenReturn(token);
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(roleName)).thenReturn(true);
		Mockito.when(roleBusinessMock.getRolePermissionsFromRedis(roleName)).thenReturn(permissionTuples);

		assertTrue(ticketBusiness.userIsAuthorized(ticket));
	}

	@Test
	public void testUserIsAuthorized_OK_2() {

		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();

		User user = UserFactoryForTests.getDefaultTestUser();
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

		String roleName = token.getUserRole();

		int numberOfPermissions = 3;
		List<PermissionTuple> permissionTuples = new ArrayList<PermissionTuple>();

		for (int i = 0; i < numberOfPermissions; i++) {

			PermissionTuple permissionTuple = new PermissionTuple("GET", "www.google.com" + i);
			permissionTuples.add(permissionTuple);
		}

		// Add the permission requested in the ticket.
		permissionTuples.add(new PermissionTuple(ticket.getHttpVerb(), ticket.getRequestedUrl()));

		Mockito.when(tokenBusinessMock.getAPIToken(ticket.getTokenValue())).thenReturn(token);
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
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

		String roleName = token.getUserRole();

		List<PermissionTuple> permissionTuples = new ArrayList<PermissionTuple>();

		Mockito.when(tokenBusinessMock.getAPIToken(ticket.getTokenValue())).thenReturn(token);
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(roleName)).thenReturn(true);

		// Return an empty list of permission tuples.
		Mockito.when(roleBusinessMock.getRolePermissionsFromRedis(roleName)).thenReturn(permissionTuples);

		assertFalse(ticketBusiness.userIsAuthorized(ticket));
	}

	@Test
	public void testUserIsAuthorized_NOK_2() {

		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();

		User user = UserFactoryForTests.getDefaultTestUser();
		Token token = new Token(ticket.getTokenValue(), user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

		String roleName = token.getUserRole();

		int numberOfPermissions = 3;
		List<PermissionTuple> permissionTuples = new ArrayList<PermissionTuple>();

		for (int i = 0; i < numberOfPermissions; i++) {

			PermissionTuple permissionTuple = new PermissionTuple("GET", "www.google.com" + i);
			permissionTuples.add(permissionTuple);
		}

		// Add the permission requested in the ticket.
		permissionTuples.add(new PermissionTuple(ticket.getHttpVerb(), ticket.getRequestedUrl()));

		Mockito.when(tokenBusinessMock.getAPIToken(ticket.getTokenValue())).thenReturn(token);
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(roleName)).thenReturn(false);
		Mockito.when(roleBusinessMock.addRolePermissionsToRedis(roleName)).thenReturn(false);

		assertFalse(ticketBusiness.userIsAuthorized(ticket));
	}
	
	@Test
	public void testIsPublicPermission_OK_1() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		String httpVerb = ticket.getHttpVerb();
		String requestedUrl = ticket.getRequestedUrl();
		
		String publicRole = EnumRoleConstants.PUBLIC_ROLE.getRoleName();
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(publicRole)).thenReturn(true);
		
		List<PermissionTuple> permissionsFromRedis = new ArrayList<PermissionTuple>();
		permissionsFromRedis.add(new PermissionTuple(httpVerb, requestedUrl));
		Mockito.when(roleBusinessMock.getRolePermissionsFromRedis(publicRole)).thenReturn(permissionsFromRedis);
		
		boolean isPublicPermission = ticketBusiness.isPublicPermission(httpVerb, requestedUrl);
		assertEquals(true, isPublicPermission);
	}
	
	@Test
	public void testIsPublicPermission_OK_2() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		String httpVerb = ticket.getHttpVerb();
		String requestedUrl = ticket.getRequestedUrl();
		
		String publicRole = EnumRoleConstants.PUBLIC_ROLE.getRoleName();
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(publicRole)).thenReturn(false).thenReturn(true);
		Mockito.when(roleBusinessMock.addRolePermissionsToRedis(publicRole)).thenReturn(true);
		
		List<PermissionTuple> permissionsFromRedis = new ArrayList<PermissionTuple>();
		permissionsFromRedis.add(new PermissionTuple(httpVerb, requestedUrl));
		Mockito.when(roleBusinessMock.getRolePermissionsFromRedis(publicRole)).thenReturn(permissionsFromRedis);
		
		boolean isPublicPermission = ticketBusiness.isPublicPermission(httpVerb, requestedUrl);
		assertEquals(true, isPublicPermission);
	}
	
	@Test
	public void testIsPublicPermission_NOK_1() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		String httpVerb = ticket.getHttpVerb();
		String requestedUrl = ticket.getRequestedUrl();
		
		String publicRole = EnumRoleConstants.PUBLIC_ROLE.getRoleName();
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(publicRole)).thenReturn(true);
		
		List<PermissionTuple> permissionsFromRedis = new ArrayList<PermissionTuple>();
		permissionsFromRedis.add(new PermissionTuple(httpVerb, "unknownUrl"));
		Mockito.when(roleBusinessMock.getRolePermissionsFromRedis(publicRole)).thenReturn(permissionsFromRedis);
		
		boolean isPublicPermission = ticketBusiness.isPublicPermission(httpVerb, requestedUrl);
		assertEquals(false, isPublicPermission);
	}
	
	@Test
	public void testIsPublicPermission_NOK_2() {
		
		Ticket ticket = TicketFactoryForTests.getDefaultTestTicket();
		String httpVerb = ticket.getHttpVerb();
		String requestedUrl = ticket.getRequestedUrl();
		
		String publicRole = EnumRoleConstants.PUBLIC_ROLE.getRoleName();
		Mockito.when(roleBusinessMock.rolePermissionsAreInRedis(publicRole)).thenReturn(false);
		Mockito.when(roleBusinessMock.addRolePermissionsToRedis(publicRole)).thenReturn(false);
		
		boolean isPublicPermission = ticketBusiness.isPublicPermission(httpVerb, requestedUrl);
		assertEquals(false, isPublicPermission);
	}
}