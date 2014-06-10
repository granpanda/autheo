package gp.e3.autheo.util;

import java.util.ArrayList;
import java.util.List;

import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;

public class RoleFactoryForTests {
	
	public static Role getDefaultTestRole() {
		
		String name = "name";
		List<Permission> permissions = new ArrayList<Permission>();
		
		return new Role(name, permissions);
	}
	
	public static Role getDefaultTestRole(int number) {
		
		String name = "name" + (number+1);
		List<Permission> permissions = PermissionFactoryForTests.getPermissionList(number);
		
		return new Role(name, permissions);
	}
	
	public static List<Role> getRoleList(int listSize) {
		
		List<Role> roleList = new ArrayList<Role>();
		
		for (int i = 0; i < listSize; i++) {
			
			roleList.add(getDefaultTestRole(i));
		}
		
		return roleList;
	}
}