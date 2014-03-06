package gp.e3.autheo.authentication.infrastructure;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

public class AuthenticationConfig extends Configuration {
	
    @NotNull
    @JsonProperty
    private DatabaseConfiguration authenticationDatabase = new DatabaseConfiguration();

	public DatabaseConfiguration getAuthenticationDatabase() {
		return authenticationDatabase;
	}
}