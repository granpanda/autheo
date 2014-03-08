package gp.e3.autheo.authentication.persistence.daos;

import redis.clients.jedis.Jedis;
import gp.e3.autheo.authentication.domain.entities.Token;

public class TokenDAO {
	
	private Jedis redisClient;
	
	public TokenDAO(String host, int port) {
		
		this.redisClient = new Jedis(host, port);
	}
	
	public String addToken(Token token) {
		
		return redisClient.set(token.getUsername(), token.getTokenValue());
	}
	
	public String getToken(String username) {
		
		return redisClient.get(username);
	}
}