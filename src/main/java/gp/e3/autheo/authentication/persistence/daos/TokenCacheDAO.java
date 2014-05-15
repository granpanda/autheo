package gp.e3.autheo.authentication.persistence.daos;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TokenCacheDAO {

	public static final String OK = "OK";
	public static final String NOK = "NOK";

	private JedisPool redisPool;

	public TokenCacheDAO(JedisPool jedisPool) {

		this.redisPool = jedisPool;
	}

	private Jedis getRedisClient(){
		return redisPool.getResource();
	}

	private void returnResource(Jedis jedis){
		redisPool.returnResource(jedis);
	}

	public boolean addTokenUsingTokenValueAsKey(Token token) {

		String answer = NOK;

		if (Token.isAValidToken(token)) {

			Jedis redisClient = getRedisClient();
			answer = redisClient.set(token.getTokenValue(), token.toString());
			returnResource(redisClient);
		}

		return answer.equalsIgnoreCase(OK);
	}

	public Token getTokenByTokenValue(String tokenValue) {

		Token token = null;

		if (StringValidator.isValidString(tokenValue)) {

			Jedis redisClient = getRedisClient();
			String tokenToString = redisClient.get(tokenValue);
			returnResource(redisClient);

			if (StringValidator.isValidString(tokenToString)) {

				token = Token.buildTokenFromTokenToString(tokenToString);
			}
		}

		return token;
	}

	public boolean addTokenUsingOrganizationAsKey(Token token) {

		String answer = NOK;

		if (Token.isAValidToken(token)) {

			Jedis redisClient = getRedisClient();
			answer = redisClient.set(token.getUserOrganization(), token.toString());
			returnResource(redisClient);
		}

		return answer.equalsIgnoreCase(OK);
	}

	public Token getTokenByOrganization(String userOrganization) {
		
		Token token = null;

		if (StringValidator.isValidString(userOrganization)) {

			Jedis redisClient = getRedisClient();
			String tokenToString = redisClient.get(userOrganization);
			returnResource(redisClient);

			if (StringValidator.isValidString(tokenToString)) {

				token = Token.buildTokenFromTokenToString(tokenToString);
			}
		}

		return token;
	}
}