package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.business.constants.TokenTypes;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authentication.persistence.daos.TokenCacheDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;

public class TokenBusiness {

	public static final String INTERNAL_API_CLIENT_ROLE = "module";

	private final BasicDataSource dataSource;
	private final TokenDAO tokenDAO;
	private final TokenCacheDAO tokenCacheDao;

	private void updateTokensCache(Connection dbConnection) throws SQLException {

		List<Token> tokens = tokenDAO.getAllTokens(dbConnection);

		for (Token tokenFromDb : tokens) {
			
			tokenCacheDao.addTokenUsingTokenValueAsKey(tokenFromDb);
				
			if (tokenFromDb.getTokenType() == TokenTypes.INTERNAL_API_TOKEN_TYPE.getTypeNumber()) {
				tokenCacheDao.addTokenUsingOrganizationAsKey(tokenFromDb);
			}
		}
	}

	public TokenBusiness(BasicDataSource basicDataSource, TokenDAO tokenDAO, TokenCacheDAO tokenCacheDao) {

		this.dataSource = basicDataSource;
		this.tokenDAO = tokenDAO;
		this.tokenCacheDao = tokenCacheDao;

		try {
			Connection dbConnection = dataSource.getConnection();
			this.tokenDAO.createTokensTableIfNotExists(dbConnection);
			updateTokensCache(dbConnection);
			dbConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Token generateRandomTokenFromUserInfo(User user, int tokenType) throws TokenGenerationException {

		String tokenValue = TokenFactory.getToken(user);
		Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId(), tokenType);

		return token;
	}

	private Token generateRandomTokenFromUserInfo(User user, String roleId, int tokenType) throws TokenGenerationException {

		String tokenValue = TokenFactory.getToken(user);
		Token token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), roleId, tokenType);

		return token;
	}

	public Token generateToken(User user) throws TokenGenerationException, IllegalArgumentException {

		Token temporalToken = null;

		if (User.isAValidUser(user)) {

			// Generate temporal token
			temporalToken = generateRandomTokenFromUserInfo(user, TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());
			tokenCacheDao.addTokenUsingTokenValueAsKey(temporalToken);

			if (user.isApiClient()) {

				try {
					
					Connection dbConnection = dataSource.getConnection();
					
					// Generate api token
					Token apiToken = generateRandomTokenFromUserInfo(user, TokenTypes.API_KEY_TOKEN_TYPE.getTypeNumber());
					tokenDAO.createToken(dbConnection, apiToken);
					tokenCacheDao.addTokenUsingTokenValueAsKey(apiToken);
					
					// Generate internal token
					Token internalToken = generateRandomTokenFromUserInfo(user, INTERNAL_API_CLIENT_ROLE, TokenTypes.INTERNAL_API_TOKEN_TYPE.getTypeNumber());
					tokenDAO.createToken(dbConnection, internalToken);
					tokenCacheDao.addTokenUsingTokenValueAsKey(internalToken);
					tokenCacheDao.addTokenUsingOrganizationAsKey(internalToken);
					
					dbConnection.close();
					
				} catch (SQLException e) {

					// Enters here if the organization already has a token into the db.
					e.printStackTrace();
				}
			}

		} else {

			String errorMessage = "The user given as argument is invalid.";
			throw new IllegalArgumentException(errorMessage);
		}

		return temporalToken;
	}

	public Token getToken(String tokenValue) {

		Token token = null;

		if (StringValidator.isValidString(tokenValue)) {

			token = tokenCacheDao.getTokenByTokenValue(tokenValue);

			if (token == null) {

				try {

					Connection dbConnection = dataSource.getConnection();
					updateTokensCache(dbConnection);
					dbConnection.close();
					
					token = tokenCacheDao.getTokenByTokenValue(tokenValue);

				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}

		return token;
	}
}