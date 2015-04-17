package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenFactory.class);
	
	public static final int TOKEN_CHARACTER_LIMIT = 20;
	
	/**
	 * Calculate and return the hash of a given string using PBKDF2WithHmacSHA1.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException 
	 */
	private static final String getHashFromString(String string) throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		byte[] salt = new byte[16];
		
		Random random = new Random();
		random.nextBytes(salt);
		
		KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, 65536, 128);
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = secretKeyFactory.generateSecret(spec).getEncoded();
		
		String stringHash = new BigInteger(1, hash).toString(16);
	
		return stringHash;
	}
	
	/**
	 * Generates the authentication token.
	 * 
	 * @param user, user information used to generate the token.
	 * @return A new authentication token. 
	 * @throws TokenGenerationException, exception thrown when there is an error generating the authentication token.
	 */
	public static final String getToken(User user) throws TokenGenerationException {

		String generatedToken = "";
		
		if (user != null) {
			
			String username = user.getUsername();
			String password = user.getPassword();
			
			if (StringValidator.isValidString(username) && StringValidator.isValidString(password)) {
				
				long currentMillis = DateTime.now().getMillis();
				String baseForToken = currentMillis + username + password;
				
				try {
					
					generatedToken = getHashFromString(baseForToken).substring(0, TOKEN_CHARACTER_LIMIT);
					
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					
					LOGGER.error("getToken", e);
					String errorMessage = "There was an error generating the authentication token.";
					throw new TokenGenerationException(errorMessage);
				}
				
			} else {
				
				String errorMessage = "The user given as argument is not valid.";
				throw new IllegalArgumentException(errorMessage);
			}
			
		} else {
			
			String errorMessage = "The user given as argument is not valid.";
			throw new IllegalArgumentException(errorMessage);
		}
		
		return generatedToken;
	}
}