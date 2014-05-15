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
	
	private boolean permissionBelongsToUserRole(PermissionTuple requestedPermission, 
			List<PermissionTuple> rolePermissionsTuples) {
		
		boolean found = false;
		
		for (int i = 0; i < rolePermissionsTuples.size() && !found; i++) {
			
			PermissionTuple currentPermission = rolePermissionsTuples.get(i);
			found = requestedPermission.areTheSamePermission(currentPermission);
		}
		
		return found;
	}
	
	public boolean userIsAuthorized(Ticket ticket) {
		
		boolean isAuthorized = false;
		
		Token retrievedToken = tokenBusiness.getToken(ticket.getTokenValue());
		String roleName = retrievedToken.getUserRole();
		
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