package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.entities.ApiUser;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.infrastructure.exceptions.CheckedIllegalArgumentException;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authentication.persistence.daos.IApiUserDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;

import java.util.ArrayList;
import java.util.List;

public class TokenBusiness {

	private IApiUserDAO apiUserDAO;
	private final TokenDAO tokenDao;

	public TokenBusiness(TokenDAO tokenDao, IApiUserDAO apiUserDAO) {

		this.tokenDao = tokenDao;
		this.apiUserDAO = apiUserDAO;
	}

	public Token generateAndSaveTokenInCache(User user) throws TokenGenerationException, CheckedIllegalArgumentException {

		Token token = null;

		if (User.isAValidUser(user)) {

			String tokenValue = TokenFactory.getToken(user);
			token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId());
			tokenDao.addToken(token);

		} else {

			String errorMessage = "The user given as argument is not valid.";
			throw new CheckedIllegalArgumentException(errorMessage);
		}

		return token;
	}

	private Token getTokenFromApiUser(ApiUser apiUser) throws TokenGenerationException, CheckedIllegalArgumentException {

		Token token = null;

		if (ApiUser.isValidApiUser(apiUser)) {

			String tokenValue = TokenFactory.getToken(apiUser);
			token = new Token(tokenValue, apiUser.getUsername(), apiUser.getOrganizationId(), apiUser.getRoleId());

		} else {

			String errorMessage = "The given api user is not valid.";
			throw new CheckedIllegalArgumentException(errorMessage);
		}

		return token;
	}

	private List<Token> getMultipleTokensFromApiUsers(List<ApiUser> apiUsersList) {

		List<Token> tokens = new ArrayList<Token>();

		for (ApiUser apiUser: apiUsersList) {

			try {
				tokens.add(getTokenFromApiUser(apiUser));
			} catch (TokenGenerationException | CheckedIllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		return tokens;
	}

	public Token getToken(String tokenValue) throws CheckedIllegalArgumentException {

		Token requestedToken = null;

		if (StringValidator.isValidString(tokenValue)) {

			requestedToken = tokenDao.getToken(tokenValue);

			if (requestedToken == null) {

				List<ApiUser> apiUsers = apiUserDAO.getAllApiUsers();
				List<Token> tokensFromApiUsers = getMultipleTokensFromApiUsers(apiUsers);
				tokenDao.addMultipleTokens(tokensFromApiUsers);

				requestedToken = tokenDao.getToken(tokenValue);
			}

		} else {

			String errorMessage = "The given token value was not valid.";
			throw new CheckedIllegalArgumentException(errorMessage);
		}

		return requestedToken;
	}
}