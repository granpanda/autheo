package gp.e3.autheo.authentication.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	
	private final String name;
	private final String username;
	private final String password;
	
	@JsonCreator
	public User(@JsonProperty("name") String name, @JsonProperty("username") String username,
				@JsonProperty("password") String password) {
	
		this.name = name;
		this.username = username;
		this.password = password;
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
}