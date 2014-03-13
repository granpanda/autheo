package gp.e3.autheo.authorization.persistence.daos;

import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;

import java.util.List;

public interface IRoleDAO {

	public void createRolesTable();
	
	public void createRole(Role role);
	
	public Role getRoleByName(String roleName);
	
	public List<String> getAllRolesNames();
	
	public int updateRole(String roleName, List<Permission> updatedPermissions);
	
	public int deleteRole(String roleName);
	
	public int addUserToRole(String username, String roleName);
	
	public int removeUserFromRole(String username, String roleName);
}