package gp.e3.autheo.authorization.domain.entities;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Role {
	
	private final String name;
	private final List<Permission> permissions;
	
	public Role(String name) {
		
		this.name = name;
		permissions = new ArrayList<Permission>();
	}
	
	@JsonCreator
	public Role(@JsonProperty("name") String name, @JsonProperty("permissions") List<Permission> permissions) {
		
		this.name = name;
		this.permissions = permissions;
	}

	public String getName() {
		return name;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}
	
	public static boolean isValidRole(Role role) {
		
		return (role!= null) && (StringValidator.isValidString(role.getName())) && (role.getPermissions() != null);
	}
}