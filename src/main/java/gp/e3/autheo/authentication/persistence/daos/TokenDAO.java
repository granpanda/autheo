package gp.e3.autheo.authentication.persistence.daos;

import redis.clients.jedis.Jedis;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

public class TokenDAO {
	
	private final Jedis redisClient;
	
	public TokenDAO(Jedis jedis) {
		
		this.redisClient = jedis;
	}
	
	public String addToken(Token token) throws IllegalArgumentException {
		
		if (Token.isAValidToken(token)) {
			
			return redisClient.set(token.getUsername(), token.toString());
			
		} else {
			
			String errorMessage = "The given token is not valid.";
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	public Token getToken(String username) throws IllegalArgumentException {
		
		if (StringValidator.isValidString(username)) {
			
			String tokenToString = redisClient.get(username);
			return Token.buildTokenFromTokenToString(tokenToString);
			
		} else {
			
			String errorMessage = "The given username is not valid.";
			throw new IllegalArgumentException(errorMessage);
		}
	}
}