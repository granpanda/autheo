package gp.e3.autheo.client.internal.token;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.client.exceptions.InvalidStateException;
import gp.e3.autheo.client.filter.TokenDTO;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public enum TokenHandlerSingleton {
	
	INSTANCE;
	
	public static final String NIL = "nil";
	public static final String OK = "OK";
	
	private JedisPool redisPool;

	private boolean singletonHasBeenInitialized() {
		
		return (redisPool != null);
	}
	
	public void initializeSingleton(JedisPool jedisPool) {
		
		if (!singletonHasBeenInitialized()) {
			
			redisPool = jedisPool;
		}
	}
	
	public TokenDTO getInternalApiTokenFromRedis(String sellerId) throws InvalidStateException {
		
		TokenDTO tokenDTO = null;
		
		if (singletonHasBeenInitialized()) {
			
			Jedis redisClient = redisPool.getResource();
			String tokenToString = redisClient.get(sellerId);
			redisPool.returnResource(redisClient);
			
			if (StringValidator.isValidString(tokenToString) && !tokenToString.equalsIgnoreCase(NIL)) {
				tokenDTO = TokenDTO.buildTokenDTOFromTokenToString(tokenToString);
			}
			
		} else {
			
			String errorMessage = "The singleton has not been initialized by first time.";
			throw new InvalidStateException(errorMessage);
		}
		
		return tokenDTO;
	}
}