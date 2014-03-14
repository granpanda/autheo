package gp.e3.autheo.authentication.domain.entities;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User implements Comparable<User> {
	
	private final String name;
	private final String username;
	private final String password;
	
	private final String userOrganizationName;
	
	@JsonCreator
	public User(@JsonProperty("name") String name, @JsonProperty("username") String username,
				@JsonProperty("password") String password, 
				@JsonProperty("userOrganizationId") String userOrganizationName) {
	
		this.name = name;
		this.username = username;
		this.password = password;
		
		this.userOrganizationName = userOrganizationName;
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
	
	public String getUserOrganizationName() {

		String answer = userOrganizationName;
		
		// If the user does not belong to a organization then return its username.
		if (!StringValidator.isValidString(answer)) {
			
			answer = username;
		}
		
		return answer;
	}

	public static boolean isAValidUser(User user) {
		
		return (user != null) && (StringValidator.isValidString(user.getName())) && 
				(StringValidator.isValidString(user.getUsername())) && 
				(StringValidator.isValidString(user.getPassword())) &&
				(StringValidator.isValidString(user.getUserOrganizationName()));
	}

	@Override
	public int compareTo(User user) {
		
		int answer = 0;
		
		if (user instanceof User) {
			
			answer += this.name.compareTo(user.getName());
			answer += this.username.compareTo(user.getUsername());
			answer += this.password.compareTo(user.getPassword());
			answer += this.userOrganizationName.compareTo(user.getUserOrganizationName());
		}
		
		return answer;
	}
}