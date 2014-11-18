package gp.e3.autheo;

import gp.e3.autheo.authentication.infrastructure.config.MySQLConfig;
import gp.e3.autheo.authentication.infrastructure.config.RedisConfig;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class AutheoConfig extends Configuration {

    @NotNull
    @JsonProperty
    private MySQLConfig mySqlConfig;

    @NotNull
    @JsonProperty
    private RedisConfig redisConfig;

    public MySQLConfig getMySqlConfig() {
	return mySqlConfig;
    }

    public RedisConfig getRedisConfig() {
	return redisConfig;
    }
}