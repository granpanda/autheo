package gp.e3.autheo.authorization.domain.business;

import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.authorization.persistence.daos.IRoleDAO;

import java.util.List;

public class RoleBusiness {

	private final IRoleDAO roleDao;
	private final PermissionBusiness permissionBusiness;

	public RoleBusiness(IRoleDAO roleDao, PermissionBusiness permissionBusiness) {

		this.roleDao = roleDao;
		this.permissionBusiness = permissionBusiness;
	}

	public Role createRole(Role role) throws DuplicateIdException {

		String roleName = role.getName();
		
		try {
			
			roleDao.createRole(roleName);
			List<Permission> rolePermissions = role.getPermissions();

			if (rolePermissions.size() > 0) {

				permissionBusiness.overwritePermissionsToRole(roleName, rolePermissions);
			}

		} catch (Exception e) {

			String errorMessage = "The role with name: " + roleName + " is already registered.";
			throw new DuplicateIdException(errorMessage);
		}
		
		return role;
	}

	public Role getRoleByName(String roleName) {

		return roleDao.getRoleByName(roleName);
	}

	public List<String> getAllRolesNames() {

		return roleDao.getAllRolesNames();
	}

	public void updateRole(String roleName, List<Permission> updatedPermissions) {

		permissionBusiness.overwritePermissionsToRole(roleName, updatedPermissions);
	}

	public void deleteRole(String roleName) {

		permissionBusiness.disassociateAllPermissionsFromRole(roleName);
		roleDao.removeAllUsersFromRole(roleName);
		roleDao.deleteRole(roleName);
	}

	public void addUserToRole(String username, String roleName) {

		roleDao.addUserToRole(username, roleName);
	}

	public void removeUserFromRole(String username, String roleName) {

		roleDao.removeUserFromRole(username);
	}
	
	public List<Permission> getAllPermissionsOfAGivenRole(String roleName) {
		
		return permissionBusiness.getAllPermissionsOfAGivenRole(roleName);
	}
}