package gp.e3.autheo.client.internal.token;

import gp.e3.autheo.client.dtos.TokenDTO;
import gp.e3.autheo.client.exceptions.InvalidStateException;
import gp.e3.autheo.client.utils.HttpUtils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.gson.Gson;

public class TokenHandler {

	private enum TokenHandlerSingleton {

		INSTANCE;

		public static final String NIL = "nil";
		public static final String SUPER_USER = "superuser";
		// public static final String OK = "OK";

		private Gson gson;
		private JedisPool redisPool;

		private boolean singletonHasBeenInitialized() {

			return (gson != null) && (redisPool != null);
		}

		public void initializeSingleton(Gson gson, JedisPool jedisPool) {

			if (!singletonHasBeenInitialized()) {
				
				this.gson = gson;
				redisPool = jedisPool;
			}
		}
		
		public TokenDTO getTokenDTOFromStringAsJSON(String stringAsJSON) {
			
			return gson.fromJson(stringAsJSON, TokenDTO.class);
		}

		public TokenDTO getModuleTokenFromRedis(String sellerId) throws InvalidStateException {

			TokenDTO tokenDTO = null;

			if (singletonHasBeenInitialized()) {

				Jedis redisClient = redisPool.getResource();
				String tokenToString = redisClient.get(sellerId);
				redisClient.close();

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

	public TokenHandler(Gson gson, JedisPool jedisPool) {
		
		TokenHandlerSingleton.INSTANCE.initializeSingleton(gson, jedisPool);
	}

	private TokenDTO getOrganizationModuleTokenFromAutheo(String organizationId) {
		
		TokenDTO moduleTokenDTO = null;
		
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		
		try {
			
			String uri = "http://localhost:9002/api/organizations/" + organizationId + "/module-token";
			HttpGet getRequest = new HttpGet(uri);
			
			String appJson = ContentType.APPLICATION_JSON.toString();
			getRequest.addHeader("Accept", appJson);
			getRequest.addHeader("Content-Type", appJson + "; charset=UTF-8");
			
			httpClient = HttpClientBuilder.create().build();
			httpResponse = httpClient.execute(getRequest);
			
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				
				String httpEntityAsString = HttpUtils.getHttpEntityAsString(httpResponse.getEntity());
				moduleTokenDTO = TokenHandlerSingleton.INSTANCE.getTokenDTOFromStringAsJSON(httpEntityAsString);
			}
			
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
		
		return moduleTokenDTO;
	}
	
	public TokenDTO getModuleTokenFromRedis(String sellerId) throws InvalidStateException {
		
		TokenDTO moduleToken = TokenHandlerSingleton.INSTANCE.getModuleTokenFromRedis(sellerId);
		
		if (moduleToken == null) {
			
			moduleToken = getOrganizationModuleTokenFromAutheo(sellerId);
		}
		
		return moduleToken;
	}
	
	public TokenDTO getSuperUserTokenFromRedis() throws InvalidStateException {
		
		String superUserValue = TokenHandlerSingleton.SUPER_USER;
		TokenDTO superUserToken = TokenHandlerSingleton.INSTANCE.getModuleTokenFromRedis(superUserValue);
		
		if (superUserToken == null) {
			
			superUserToken = getOrganizationModuleTokenFromAutheo(superUserValue);
		}
		
		return superUserToken;
	}
}