package gp.e3.autheo;

import gp.e3.autheo.authentication.infrastructure.AuthenticationConfig;
import gp.e3.autheo.authentication.infrastructure.RedisConfig;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class AutheoConfig extends Configuration {
	
    @NotNull
    @JsonProperty
    AuthenticationConfig authenticationConfig = new AuthenticationConfig();
    
    @NotNull
    @JsonProperty
    private RedisConfig redisConfig;

	public AuthenticationConfig getAuthenticationConfig() {
		return authenticationConfig;
	}
	
	public RedisConfig getRedisConfig() {
		return redisConfig;
	}
}