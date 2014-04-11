package gp.e3.autheo.authentication.persistence.daos;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

public class TokenDAO {
	
	private JedisPool redisPool;
	
	public TokenDAO(JedisPool jedisPool) {
		
		this.redisPool = jedisPool;
	}
	
	private Jedis getRedisClient(){
		return redisPool.getResource();
	}
	
	private void returnBrokenResource(Jedis jedis){
		redisPool.returnBrokenResource(jedis);
	}
	
	private void returnResource(Jedis jedis){
		redisPool.returnResource(jedis);
	}
	
	public String addToken(Token token) throws IllegalArgumentException {
		Jedis redisClient = null;
		try {
			redisClient = getRedisClient();
		
			if (Token.isAValidToken(token)) {
				return redisClient.set(token.getTokenValue(), token.toString());
				
			} else {
				
				String errorMessage = "The given token is not valid.";
				throw new IllegalArgumentException(errorMessage);
			}
		} catch(JedisConnectionException e) {
			returnBrokenResource(redisClient);
			throw e;
		} finally {
			if ( redisClient != null ) {
			    returnResource(redisClient);
			}
		}
	}
	
	public Token getToken(String tokenValue) throws IllegalArgumentException {
		Jedis redisClient = null;
		try {
			redisClient = getRedisClient();
			if (StringValidator.isValidString(tokenValue)) {
				
				String tokenToString = redisClient.get(tokenValue);
				
				if (StringValidator.isValidString(tokenToString)) {
					
					return Token.buildTokenFromTokenToString(tokenToString);
					
				} else {
					
					String errorMessage = "The given token is not valid.";
					throw new IllegalArgumentException(errorMessage);
				}
				
			} else {
				
				String errorMessage = "The given token is not valid.";
				throw new IllegalArgumentException(errorMessage);
			}
		} catch(JedisConnectionException e) {
			returnBrokenResource(redisClient);
			throw e;
		} finally {
			if ( redisClient != null ) {
			    returnResource(redisClient);
			}
		}
	}
}