package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.business.constants.TokenTypes;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;
import gp.e3.autheo.authentication.domain.exceptions.ValidDataException;
import gp.e3.autheo.authentication.infrastructure.utils.SqlUtils;
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

	public void updateTokensCache() throws SQLException {

		Connection dbConnection = dataSource.getConnection();

		List<Token> tokens = tokenDAO.getAllTokens(dbConnection);

		for (Token tokenFromDb : tokens) {

			tokenCacheDao.addTokenUsingTokenValueAsKey(tokenFromDb);

			if (tokenFromDb.getTokenType() == TokenTypes.INTERNAL_API_TOKEN_TYPE.getTypeNumber()) {
				tokenCacheDao.addTokenUsingOrganizationAsKey(tokenFromDb);
			}
		}

		dbConnection.close();
	}

	public TokenBusiness(BasicDataSource basicDataSource, TokenDAO tokenDAO, TokenCacheDAO tokenCacheDao) {

		this.dataSource = basicDataSource;
		this.tokenDAO = tokenDAO;
		this.tokenCacheDao = tokenCacheDao;

		Connection dbConnection = null;

		try {
			dbConnection = dataSource.getConnection();
			this.tokenDAO.createTokensTableIfNotExists(dbConnection);
			updateTokensCache();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			SqlUtils.closeDbConnection(dbConnection);
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

	private Token generateSaveAndGetAPIToken(BasicDataSource dataSource, User user) {

		Token apiToken = null;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			apiToken = generateRandomTokenFromUserInfo(user, TokenTypes.API_KEY_TOKEN_TYPE.getTypeNumber());
			tokenDAO.createToken(dbConnection, apiToken);
			tokenCacheDao.addTokenUsingTokenValueAsKey(apiToken);

		} catch (TokenGenerationException | SQLException e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return apiToken;
	}

	private Token generateSaveAndGetInternalAPIToken(BasicDataSource dataSource, User user) {

		Token internalAPIToken = null;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			internalAPIToken = generateRandomTokenFromUserInfo(user, INTERNAL_API_CLIENT_ROLE, TokenTypes.INTERNAL_API_TOKEN_TYPE.getTypeNumber());
			tokenDAO.createToken(dbConnection, internalAPIToken);
			tokenCacheDao.addTokenUsingTokenValueAsKey(internalAPIToken);
			tokenCacheDao.addTokenUsingOrganizationAsKey(internalAPIToken);

		} catch (TokenGenerationException | SQLException e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return internalAPIToken;
	}
	
	public boolean generateAndSaveTokensForAnAPIUser(User user) {

		boolean tokensWereGeneratedAndSaved = false;

		if (User.isAValidUser(user) && user.isApiClient()) {

			Token apiToken = generateSaveAndGetAPIToken(dataSource, user);
			Token internalAPIToken = generateSaveAndGetInternalAPIToken(dataSource, user);
			tokensWereGeneratedAndSaved = (apiToken != null && internalAPIToken != null);
		}

		return tokensWereGeneratedAndSaved;
	}

	public Token generateToken(User user) throws TokenGenerationException, IllegalArgumentException {

		Token temporalToken = null;

		if (User.isAValidUser(user)) {

			// Generate temporal token
			temporalToken = generateRandomTokenFromUserInfo(user, TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());
			tokenCacheDao.addTokenUsingTokenValueAsKey(temporalToken);

		} else {

			String errorMessage = "The user given as argument is invalid.";
			throw new IllegalArgumentException(errorMessage);
		}

		return temporalToken;
	}

	public Token getAPIToken(String tokenValue) {

		Token token = null;

		if (StringValidator.isValidString(tokenValue)) {

			token = tokenCacheDao.getTokenByTokenValue(tokenValue);

			if (token == null) {

				try {
					updateTokensCache();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				token = tokenCacheDao.getTokenByTokenValue(tokenValue);
			}
		}

		return token;
	}
	
	public Token getModuleToken(String userOrganizationId) {
	
		Token token = null;

		if (StringValidator.isValidString(userOrganizationId)) {

			token = tokenCacheDao.getTokenByOrganization(userOrganizationId);

			if (token == null) {

				try {
					updateTokensCache();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				token = tokenCacheDao.getTokenByOrganization(userOrganizationId);
			}
		}

		return token;
	}

	public boolean removeUserAccessToken(String tokenValue) throws ValidDataException{
		boolean tokenWasRemoved = false;

		if(StringValidator.isValidString(tokenValue)){
			tokenWasRemoved = tokenCacheDao.removeUserAccessToken(tokenValue);
		} else {
			throw new ValidDataException("Invalid token value");
		}

		return tokenWasRemoved;
	}
}