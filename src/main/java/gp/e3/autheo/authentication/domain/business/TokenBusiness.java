package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authentication.persistence.daos.TokenCacheDAO;

public class TokenBusiness {
	
	private final TokenCacheDAO tokenDao;
	
	public TokenBusiness(TokenCacheDAO tokenDao) {
		this.tokenDao = tokenDao;
	}

	public Token generateToken(User user) throws TokenGenerationException, IllegalArgumentException {
		
		Token token = null;
		
		if (User.isAValidUser(user)) {
		
			String tokenValue = TokenFactory.getToken(user);
			token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			tokenDao.addToken(token);
			
		} else {
			
			String errorMessage = "The user given as argument is invalid.";
			throw new IllegalArgumentException(errorMessage);
		}
		
		return token;
	}
	
	public Token getToken(String tokenValue) throws IllegalArgumentException {
		
		if (StringValidator.isValidString(tokenValue)) {
			
			return tokenDao.getToken(tokenValue);
			
		} else {
			
			String errorMessage = "The parameter is null or empty.";
			throw new IllegalArgumentException(errorMessage);
		}
	}
}