package gp.e3.autheo.authorization.domain.business;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.authorization.infrastructure.dtos.PermissionTuple;
import gp.e3.autheo.authorization.persistence.daos.IRoleDAO;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;

public class RoleBusiness {
	
	private final IRoleDAO roleDao;
	private final PermissionBusiness permissionBusiness;
	
	private final Jedis redisClient;

	public RoleBusiness(IRoleDAO roleDao, PermissionBusiness permissionBusiness, Jedis jedis) {

		this.roleDao = roleDao;
		this.permissionBusiness = permissionBusiness;
		
		this.roleDao.createRolesTable();
		this.roleDao.createRolesAndPermissionsTable();
		this.roleDao.createRolesAndUsersTable();
		
		redisClient = jedis;
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
		
		List<Permission> rolePermissions = permissionBusiness.getAllPermissionsOfAGivenRole(roleName);
		return new Role(roleName, rolePermissions);
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
	
	public boolean rolePermissionsAreInRedis(String roleName) {
		
		String getResult = redisClient.get(roleName);
		
		System.out.println("areRolePermissionsInRedis()");
		System.out.println(getResult);
		
		return StringValidator.isValidString(getResult);
	}
	
	public boolean addRolePermissionsToRedis(String roleName) {
		
		List<Permission> rolePermissions = permissionBusiness.getAllPermissionsOfAGivenRole(roleName);
		
		String permssionsString = "";
		
		for (Permission permission : rolePermissions) {
			
			permssionsString += permission.toString() + Permission.PERMISSION_SPLIT;
		}
		
		String setResult = redisClient.set(roleName, permssionsString);
		
		System.out.println("addRolePermissionsToRedis");
		System.out.println(setResult);
		
		return true;
	}
	
	public List<PermissionTuple> getRolePermissionsFromRedis(String roleName) {
		
		String rolePermissionsString = redisClient.get(roleName);
		String[] permissionsArray = rolePermissionsString.split(Permission.PERMISSION_SPLIT);
		
		List<PermissionTuple> rolePermissions = new ArrayList<PermissionTuple>();
		
		for (int i = 0; i < permissionsArray.length; i++) {
			
			String permissionString = permissionsArray[i];
			String[] permissionAttributes = permissionString.split(Permission.ATTRIBUTE_SPLIT);
			rolePermissions.add(new PermissionTuple(permissionAttributes[2], permissionAttributes[3]));
		}
		
		return rolePermissions;
	}
}