package gp.e3.autheo.authentication.infrastructure.healthchecks;

import redis.clients.jedis.Jedis;

import com.yammer.metrics.core.HealthCheck;

public class RedisHealthCheck extends HealthCheck {

	private Jedis redisClient;
	
	public RedisHealthCheck(String name, Jedis redisClient) {
		
		super(name);
		this.redisClient = redisClient;
	}

	@Override
	protected Result check() throws Exception {
		
		return redisClient.isConnected()? Result.healthy() : Result.unhealthy("Redis client is not connected.");
	}
}