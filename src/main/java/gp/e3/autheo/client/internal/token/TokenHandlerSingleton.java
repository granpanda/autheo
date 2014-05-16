package gp.e3.autheo.client.internal.token;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.client.exceptions.InvalidStateException;
import gp.e3.autheo.client.filter.TokenDTO;
import redis.clients.jedis.Jedis;

public enum TokenHandlerSingleton {
	
	INSTANCE;
	
	public static final String NIL = "nil";
	public static final String OK = "OK";
	
	private String tokenValue; 
	private Jedis redisClient;

	private boolean singletonHasBeenInitialized() {
		
		return (this.tokenValue != null && redisClient != null);
	}
	
	public void initializeSingleton(Jedis redisClient, String tokenValue) {
		
		if (!singletonHasBeenInitialized()) {
			
			this.tokenValue = tokenValue;
			this.redisClient = redisClient;
		}
	}
	
	public TokenDTO getInternalApiTokenFromRedis(String sellerId) throws InvalidStateException {
		
		TokenDTO tokenDTO = null;
		
		if (singletonHasBeenInitialized()) {
			
			String tokenToString = redisClient.get(sellerId);
			
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