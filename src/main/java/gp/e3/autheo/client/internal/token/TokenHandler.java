package gp.e3.autheo.client.internal.token;

import gp.e3.autheo.client.exceptions.InvalidStateException;
import gp.e3.autheo.client.filter.TokenDTO;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TokenHandler {

	private enum TokenHandlerSingleton {

		INSTANCE;

		public static final String NIL = "nil";
		// public static final String OK = "OK";

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

				if (!StringUtils.isBlank(tokenToString) && !tokenToString.equalsIgnoreCase(NIL)) {
					tokenDTO = TokenDTO.buildTokenDTOFromTokenToString(tokenToString);
				}

			} else {

				String errorMessage = "The singleton has not been initialized by first time.";
				throw new InvalidStateException(errorMessage);
			}

			return tokenDTO;
		}
	}

	public TokenHandler(JedisPool jedisPool) {
		TokenHandlerSingleton.INSTANCE.initializeSingleton(jedisPool);
	}

	private boolean updateTokensCacheInAutheo() {
		
		boolean tokensCacheWasUpdated = false;
		
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		
		try {
			
			String uri = "http://localhost:9002/api/tokens";
			HttpPut putRequest = new HttpPut(uri);
			
			String appJson = ContentType.APPLICATION_JSON.toString();
			putRequest.addHeader("Accept", appJson);
			putRequest.addHeader("Content-Type", appJson + "; charset=UTF-8");
			
			httpClient = HttpClientBuilder.create().build();
			httpResponse = httpClient.execute(putRequest);
			
			tokensCacheWasUpdated = (httpResponse.getStatusLine().getStatusCode() == 200);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		} finally {
			
			try {
				httpClient.close();
				httpResponse.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		return tokensCacheWasUpdated;
	}
	
	public TokenDTO getInternalApiTokenFromRedis(String sellerId) throws InvalidStateException {
		
		TokenDTO internalApiToken = TokenHandlerSingleton.INSTANCE.getInternalApiTokenFromRedis(sellerId);
		
		if (internalApiToken == null) {
			
			if (updateTokensCacheInAutheo()) {
				internalApiToken = TokenHandlerSingleton.INSTANCE.getInternalApiTokenFromRedis(sellerId);
			}
		}
		
		return internalApiToken;
	}
}