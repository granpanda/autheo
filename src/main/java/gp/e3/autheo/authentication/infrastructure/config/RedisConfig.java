package gp.e3.autheo.authentication.infrastructure.config;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class RedisConfig extends Configuration {
	
	@NotNull
	@NotEmpty
    @JsonProperty
	private String host;
	
	@NotNull
	@NotEmpty
    @JsonProperty
	private int port;
	
	@NotNull
	@NotEmpty
    @JsonProperty
	private int database;

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getDatabase() {
		return database;
	}
}