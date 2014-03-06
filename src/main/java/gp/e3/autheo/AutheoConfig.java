package gp.e3.autheo;

import gp.e3.autheo.authentication.infrastructure.AuthenticationConfig;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class AutheoConfig extends Configuration {
	
    @NotNull
    @JsonProperty
    AuthenticationConfig authenticationConfig = new AuthenticationConfig();

	public AuthenticationConfig getAuthenticationConfig() {
		return authenticationConfig;
	}
}