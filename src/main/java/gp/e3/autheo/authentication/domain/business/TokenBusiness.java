package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authentication.persistence.daos.ITokenDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenCacheDAO;

import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

public class TokenBusiness {

	public static final String INTERNAL_API_CLIENT_ROLE = "MODULE";

	private final ITokenDAO tokenDAO;
	private final TokenCacheDAO tokenCacheDao;

	public TokenBusiness(ITokenDAO tokenDAO, TokenCacheDAO tokenCacheDao) {

		this.tokenDAO = tokenDAO;
		this.tokenDAO.createTokensTableIfNotExists();
		
		this.tokenCacheDao = tokenCacheDao;
	}

	private Token generateRandomTokenFromUserInfo(User user) throws TokenGenerationException {

		String tokenValue = TokenFactory.getToken(user);
		Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());

		return token;
	}

	private Token generateRandomTokenFromUserInfo(User user, String roleId) throws TokenGenerationException {

		String tokenValue = TokenFactory.getToken(user);
		Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), roleId);

		return token;
	}

	public Token generateToken(User user) throws TokenGenerationException, IllegalArgumentException {

		Token token = null;

		if (User.isAValidUser(user)) {

			token = generateRandomTokenFromUserInfo(user);
			tokenCacheDao.addTokenUsingTokenValueAsKey(token);

			if (user.isApiClient()) {

				try {
					// Add token to DB.
					tokenDAO.createToken(token.getTokenValue(), token.getUsername(), token.getUserOrganization(), token.getUserRole());
					
					// Create a new API token. Save it to DB and Cache.
					Token apiClientToken = generateRandomTokenFromUserInfo(user, INTERNAL_API_CLIENT_ROLE);
					tokenDAO.createToken(apiClientToken.getTokenValue(), apiClientToken.getUsername(), apiClientToken.getUserOrganization(), apiClientToken.getUserRole());
					tokenCacheDao.addTokenUsingOrganizationAsKey(apiClientToken);
					
				} catch (UnableToExecuteStatementException e) {
					e.printStackTrace();
				}
			}

		} else {

			String errorMessage = "The user given as argument is invalid.";
			throw new IllegalArgumentException(errorMessage);
		}

		return token;
	}

	public Token getToken(String tokenValue) {

		Token token = null;
		
		if (StringValidator.isValidString(tokenValue)) {

			token = tokenCacheDao.getTokenByTokenValue(tokenValue);
		}
		
		return token;
	}
}