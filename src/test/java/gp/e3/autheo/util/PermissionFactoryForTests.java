package gp.e3.autheo.util;

import java.util.ArrayList;
import java.util.List;

import gp.e3.autheo.authorization.domain.entities.Permission;

public class PermissionFactoryForTests {
	
	public static final Permission getDefaultTestPermission() {
		
		int id = 0;
		String name = "name";
		String httpVerb = "GET";
		String url = "www.google.com";
		
		return new Permission(id, name, httpVerb, url);
	}
	
	public static final Permission getDefaultTestPermission(int number) {
		
		int id = 0 + (number+1);
		String name = "name" + number;
		String httpVerb = "GET" + number;
		String url = "www.google.com" + number;
		
		return new Permission(id, name, httpVerb, url);
	}
	
	public static final List<Permission> getPermissionList(int listSize) {
		
		List<Permission> permissionList = new ArrayList<Permission>();
		
		for (int i = 0; i < listSize; i++) {
			
			permissionList.add(getDefaultTestPermission(i));
		}
		
		return permissionList;
	}
}