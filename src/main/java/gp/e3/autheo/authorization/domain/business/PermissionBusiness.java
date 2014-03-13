package gp.e3.autheo.authorization.domain.business;

import java.util.ArrayList;
import java.util.List;

import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.persistence.daos.IPermissionDAO;

public class PermissionBusiness {
	
	private final IPermissionDAO permissionDao;

	public PermissionBusiness(IPermissionDAO permissionDao) {
		
		this.permissionDao = permissionDao;
	}
	
	public Permission createPermission(Permission permission) throws DuplicateIdException {
		
		try {
			
			permissionDao.createPermission(permission.getName(), permission.getHttpVerb(), permission.getUrl());
			
		} catch (Exception e) {
			
			String errorMessage = "The permission with http verb: " + permission.getHttpVerb() +
					" and url: " + permission.getUrl() + " is alredy registered.";
			throw new DuplicateIdException(errorMessage);
		}
		
		return permission;
	}
	
	public void overwritePermissionsToRole(String roleName, List<Permission> permissions) {
		
		List<Integer> permissionsIds = new ArrayList<Integer>();
		
		for (Permission permission : permissions) {
			
			permissionsIds.add(permission.getId());
		}
		
		permissionDao.disassociateAllPermissionsFromRole(roleName);
		permissionDao.associateAllPermissionsToRole(roleName, permissionsIds);
	}
	
public void disassociatePermissionFromAllRoles(String permissionId) {
		
		permissionDao.disassociatePermissionFromAllRoles(permissionId);
	}
	
	public void disassociateAllPermissionsFromRole(String roleName) {
		
		permissionDao.disassociateAllPermissionsFromRole(roleName);
	}
	
	public Permission getPermissionById(String permissionId) {
		
		return permissionDao.getPermissionById(permissionId);
	}
	
	public Permission getPermissionByHttpVerbAndUrl(String httpVerb, String url) {
		
		return permissionDao.getPermissionByHttpVerbAndUrl(httpVerb, url);
	}
	
	public List<Permission> getAllPermissions() {
		
		return permissionDao.getAllPermissions();
	}
	
	public List<Permission> getAllPermissionsOfAGivenRole(String roleName) {
		
		return permissionDao.getAllPermissionsOfAGivenRole(roleName);
	}
	
	public int deletePermission(String permissionId) {
		
		permissionDao.disassociatePermissionFromAllRoles(permissionId);
		return permissionDao.deletePermission(permissionId);
	}
}