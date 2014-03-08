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

	public Token generateToken(User user) throws TokenGenerationException {
		
		String tokenValue = TokenFactory.getToken(user);
		Token token = new Token(user.getUsername(), tokenValue);
		tokenDao.addToken(token);
		
		return token;
	}
	
	public String getTokenValue(String username) {
		
		return tokenDao.getToken(username);
	}
}