package gp.e3.autheo.authorization.domain.business;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authorization.domain.entities.Ticket;
import gp.e3.autheo.authorization.infrastructure.dtos.PermissionTuple;

import java.util.List;

public class TicketBusiness {
	
	private final TokenBusiness tokenBusiness;
	private final RoleBusiness roleBusiness;
	
	public TicketBusiness(TokenBusiness tokenBusiness, RoleBusiness roleBusiness) {
		
		this.tokenBusiness = tokenBusiness;
		this.roleBusiness = roleBusiness;
	}

	public Token tokenWasIssuedByUs(Ticket ticket) {
		
		Token answerToken = null;
		
		if (ticket != null && StringValidator.isValidString(ticket.getTokenValue())) {
			
			try {
				
				answerToken = tokenBusiness.getToken(ticket.getTokenValue());
				
			} catch (Exception e) {
				
				e.printStackTrace();
				// Do nothing, the answer is false.
			}
		}
		
		return answerToken;
	}
	
	private boolean permissionBelongsToUserRole(PermissionTuple requestedPermission, List<PermissionTuple> rolePermissionsTuples) {
		
		boolean found = false;
		
		System.out.println("********************************* 3");
		
		for (int i = 0; i < rolePermissionsTuples.size() && !found; i++) {
			
			PermissionTuple currentPermission = rolePermissionsTuples.get(i);
			
			System.out.println("Current permission:");
			System.out.println(currentPermission.getHttpVerb());
			System.out.println(currentPermission.getUrl());
			
			System.out.println();
			
			System.out.println("Requested permission");
			System.out.println(requestedPermission.getHttpVerb());
			System.out.println(requestedPermission.getUrl());
			
			System.out.println();
			System.out.println();
			
			found = requestedPermission.areTheSamePermission(currentPermission);
		}
		
		System.out.println("****************************** 4");
		System.out.println("Found? " + found);
		
		return found;
	}
	
	public boolean userIsAuthorized(Ticket ticket) {
		
		boolean isAuthorized = false;
		
		Token retrievedToken = tokenBusiness.getToken(ticket.getTokenValue());
		String roleName = retrievedToken.getUserRole();
		
		System.out.println("********************************* 2");
		System.out.println("Role permissions are in redis: " + roleBusiness.rolePermissionsAreInRedis(roleName));
		
		if (roleBusiness.rolePermissionsAreInRedis(roleName)) {
			
			PermissionTuple requestedPermission = new PermissionTuple(ticket.getHttpVerb(), ticket.getRequestedUrl());
			List<PermissionTuple> rolePermissionsTuples = roleBusiness.getRolePermissionsFromRedis(roleName);
			isAuthorized = permissionBelongsToUserRole(requestedPermission, rolePermissionsTuples);
			
		} else {
			
			if (roleBusiness.addRolePermissionsToRedis(roleName)) {
				
				isAuthorized = userIsAuthorized(ticket); // Recursion.
			}
		}
		
		return isAuthorized;
	}
}