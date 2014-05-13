package gp.e3.autheo.authentication.persistence.daos;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.infrastructure.exceptions.CheckedIllegalArgumentException;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TokenDAO {

	// See: http://tool.oschina.net/uploads/apidocs/jedis-2.1.0/index.html?redis/clients/jedis/Jedis.html -> get(String key) docs.
	public static final String NIL = "nil";
	public static final String NOK = "NOK";

	private JedisPool redisPool;

	public TokenDAO(JedisPool jedisPool) {
		this.redisPool = jedisPool;
	}

	private Jedis getRedisClient() {
		return redisPool.getResource();
	}

	private void returnResource(Jedis jedis){
		redisPool.returnResource(jedis);
	}

	public String addToken(Token token) throws CheckedIllegalArgumentException {

		Jedis redisClient = null;
		redisClient = getRedisClient();

		String redisAnswer = NOK;
		
		if (Token.isAValidToken(token)) {
			
			redisAnswer = redisClient.set(token.getTokenValue(), token.toString());
			
		} else { 
			
			String errorMessage = "The given token was not valid.";
			throw new CheckedIllegalArgumentException(errorMessage);
		}

		returnResource(redisClient);
		
		return redisAnswer;
	}

	public void addMultipleTokens(List<Token> tokenList) {

		Jedis redisClient = null;

		redisClient = getRedisClient();

		for (Token token : tokenList) {

			if (Token.isAValidToken(token)) {
				redisClient.set(token.getTokenValue(), token.toString());
			}
		}

		returnResource(redisClient);
	}

	public Token getToken(String tokenValue) throws CheckedIllegalArgumentException {

		Jedis redisClient = null;

		Token token = null;

		redisClient = getRedisClient();

		if (StringValidator.isValidString(tokenValue)) {

			String tokenToString = redisClient.get(tokenValue);

			if (!tokenToString.equalsIgnoreCase(NIL)) {

				try {
					
					token = Token.buildTokenFromTokenToString(tokenToString);
					
				} catch (CheckedIllegalArgumentException e) {
					
					e.printStackTrace();
				}
			}
			
		} else {
			
			String errorMessage = "The given token value was not valid.";
			throw new CheckedIllegalArgumentException(errorMessage);
		}
		
		returnResource(redisClient);
		
		return token;
	}
}