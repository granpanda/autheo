package gp.e3.autheo.authentication.persistence.daos;

import redis.clients.jedis.Jedis;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

public class TokenDAO {
	
	private final Jedis redisClient;
	
	public TokenDAO(Jedis jedis) {
		
		this.redisClient = jedis;
	}
	
	private boolean isAValidToken(Token token) {
		
		return (token != null) && (StringValidator.isValidString(token.getUsername())) 
				&& (StringValidator.isValidString(token.getTokenValue()));
	}
	
	public String addToken(Token token) throws IllegalArgumentException {
		
		if (isAValidToken(token)) {
			
			return redisClient.set(token.getUsername(), token.getTokenValue());
			
		} else {
			
			String errorMessage = "The given token is not valid.";
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	public String getToken(String username) throws IllegalArgumentException {
		
		if (StringValidator.isValidString(username)) {
			
			return redisClient.get(username);
			
		} else {
			
			String errorMessage = "The given username is not valid.";
			throw new IllegalArgumentException(errorMessage);
		}
	}
}