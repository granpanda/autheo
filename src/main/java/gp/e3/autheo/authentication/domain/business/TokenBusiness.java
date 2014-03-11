package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;

public class TokenBusiness {
	
	private final TokenDAO tokenDao;
	
	public TokenBusiness(TokenDAO tokenDao) {
		this.tokenDao = tokenDao;
	}

	public Token generateToken(User user) throws TokenGenerationException, IllegalArgumentException {
		
		Token token = null;
		
		if ((user != null) && (!user.getUsername().isEmpty())) {
		
			String tokenValue = TokenFactory.getToken(user);
			token = new Token(user.getUsername(), tokenValue);
			tokenDao.addToken(token);
			
		} else {
			
			String errorMessage = "The user given as argument is invalid.";
			throw new IllegalArgumentException(errorMessage);
		}
		
		return token;
	}
	
	public String getTokenValue(String username) throws IllegalArgumentException {
		
		if ((username != null) && (!username.isEmpty())) {
			
			return tokenDao.getToken(username);
			
		} else {
			
			String errorMessage = "The parameter is null or empty.";
			throw new IllegalArgumentException(errorMessage);
		}
	}
}