package gp.e3.autheo;

import gp.e3.autheo.authentication.infrastructure.RedisConfig;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

public class AutheoConfig extends Configuration {
	
	@NotNull
    @JsonProperty
    private DatabaseConfiguration mySqlConfig = new DatabaseConfiguration();
	
	@NotNull
    @JsonProperty
    private RedisConfig redisConfig;

	public DatabaseConfiguration getMySqlConfig() {
		return mySqlConfig;
	}
	
	public RedisConfig getRedisConfig() {
		return redisConfig;
	}
}