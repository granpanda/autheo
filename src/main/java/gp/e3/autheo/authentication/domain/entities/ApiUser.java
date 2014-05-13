package gp.e3.autheo.authentication.domain.entities;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiUser implements Comparable<ApiUser> {
	
	private final String name;
	private final String username;
	private final String password;
	private final String organizationId;
	private final String roleId;
	private final String tokenValue;
	
	@JsonCreator
	public ApiUser(@JsonProperty("name") String name, @JsonProperty("username") String username, @JsonProperty("password") String password, 
				@JsonProperty("organizationId") String organizationId, @JsonProperty("roleId") String roleId, @JsonProperty("tokenValue") String tokenValue) {
	
		this.name = name;
		this.username = username;
		this.password = password;
		this.organizationId = organizationId;
		this.roleId= roleId;
		this.tokenValue = tokenValue;
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

	public String getTokenValue() {
		return tokenValue;
	}

	public static boolean isValidApiUser(ApiUser apiUser) {
		
		return (apiUser != null) && 
				(StringValidator.isValidString(apiUser.getName())) &&
				(StringValidator.isValidString(apiUser.getUsername())) && 
				(StringValidator.isValidString(apiUser.getPassword())) &&
				(StringValidator.isValidString(apiUser.getOrganizationId())) &&
				(StringValidator.isValidString(apiUser.getRoleId())) && 
				(StringValidator.isValidString(apiUser.getTokenValue()));
	}

	@Override
	public int compareTo(ApiUser apiUser) {
		
		int answer = 0;
		
		if (apiUser instanceof ApiUser) {
			
			answer += this.name.compareTo(apiUser.getName());
			answer += this.username.compareTo(apiUser.getUsername());
			answer += this.password.compareTo(apiUser.getPassword());
			answer += this.organizationId.compareTo(apiUser.getOrganizationId());
			answer += this.roleId.compareTo(apiUser.getRoleId());
			answer += this.tokenValue.compareTo(apiUser.getTokenValue());
		}
		
		return answer;
	}
}