package gp.e3.autheo.authentication.domain.entities;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User implements Comparable<User> {
	
	private final String name;
	private final String username;
	private final String password;
	private final boolean apiClient;
	
	private final String organizationId;
	private final String roleId;
	
	@JsonCreator
	public User(@JsonProperty("name") String name, @JsonProperty("username") String username,
				@JsonProperty("password") String password, @JsonProperty("apiClient") boolean isApiClient,
				@JsonProperty("organizationId") String organizationId,
				@JsonProperty("roleId") String roleId) {
	
		this.name = name;
		this.username = username;
		this.password = password;
		this.apiClient = isApiClient;
		
		this.organizationId = organizationId;
		this.roleId= roleId;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public boolean isApiClient() {
		return apiClient;
	}

	public String getOrganizationId() {

		String answer = organizationId;
		
		// If the user does not belong to a organization then return its username.
		if (!StringValidator.isValidString(answer)) {
			
			answer = username;
		}
		
		return answer;
	}

	public String getRoleId() {
		return roleId;
	}

	public static boolean isAValidUser(User user) {
		
		return (user != null) && 
				(StringValidator.isValidString(user.getName())) &&
				(StringValidator.isValidString(user.getUsername())) && 
				(StringValidator.isValidString(user.getPassword())) &&
				(StringValidator.isValidString(user.getOrganizationId()) &&
				(StringValidator.isValidString(user.getRoleId())));
	}

	@Override
	public int compareTo(User user) {
		
		int answer = 0;
		
		if (user instanceof User) {
			
			answer += this.name.compareTo(user.getName());
			answer += this.username.compareTo(user.getUsername());
			answer += this.password.compareTo(user.getPassword());
			answer += (this.apiClient == user.isApiClient())? 0 : 1;
			answer += this.organizationId.compareTo(user.getOrganizationId());
			answer += this.roleId.compareTo(user.roleId);
		}
		
		return answer;
	}
}