package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.entities.ApiUser;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.infrastructure.exceptions.CheckedIllegalArgumentException;
import gp.e3.autheo.authentication.persistence.daos.IApiUserDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.security.sasl.AuthenticationException;

public class ApiUserBusiness {
	
	private final IApiUserDAO apiUserDao;
	private final TokenDAO tokenDAO;

	public ApiUserBusiness(IApiUserDAO apiUserDao, TokenDAO tokenDAO) {
		
		this.apiUserDao = apiUserDao;
		this.apiUserDao.createApiUsersTableIfNotExists();
		
		this.tokenDAO = tokenDAO;
	}
	
	public ApiUser createApiUser(ApiUser newApiUser) throws DuplicateIdException, CheckedIllegalArgumentException {
		
		try {
			
			String originalPassword = newApiUser.getPassword();
			String passwordHash = PasswordHandler.getPasswordHash(originalPassword);
			String passwordSalt = PasswordHandler.getSaltFromHashedAndSaltedPassword(passwordHash);
			String tokenValue = TokenFactory.getToken(newApiUser);
			
			// Add the api user to MySQL.
			apiUserDao.createApiUser(newApiUser.getName(), newApiUser.getUsername(), passwordHash, passwordSalt, 
					newApiUser.getOrganizationId(), newApiUser.getRoleId(), tokenValue);
			
			// Add api user token to Redis.
			Token token = new Token(tokenValue, newApiUser.getUsername(), newApiUser.getOrganizationId(), newApiUser.getRoleId());
			tokenDAO.addToken(token);
			
		} catch (CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
			throw new CheckedIllegalArgumentException(e.getMessage());
			
		} catch (Exception e) {
			
			e.printStackTrace();
			String errorMessage = "The api user with username: " + newApiUser.getUsername() + " is already registered.";
			throw new DuplicateIdException(errorMessage);
		}
		
		return newApiUser;
	}
	
	public boolean authenticateApiUser(String username, String password) throws AuthenticationException {
		
		boolean isAuthenticated = false;
		String errorMessage = "The user credentials are not valid.";
		
		String passwordHashFromDb = apiUserDao.getPasswordByUsername(username);
		
		if (passwordHashFromDb != null) {
			
			try {
				
				isAuthenticated = PasswordHandler.validatePassword(password, passwordHashFromDb);
				
			} catch (NoSuchAlgorithmException | InvalidKeySpecException	| CheckedIllegalArgumentException e) {
				
				e.printStackTrace();
				throw new AuthenticationException(errorMessage);
			}
			
		} else {
			
			throw new AuthenticationException(errorMessage);
		}
		
		return isAuthenticated;
	}
	
	public ApiUser getApiUserByUsername(String username) {
		
		return apiUserDao.getApiUserByUsername(username);
	}
	
	public ApiUser getApiUserByToken(String tokenValue) {
		
		return apiUserDao.getApiUserByToken(tokenValue);
	}
	
	public List<ApiUser> getAllApiUsers() {
		
		return apiUserDao.getAllApiUsers();
	}
	
	public void updateApiUser(String username, ApiUser updatedApiUser) {
		
		try {
		
			String originalPassword = updatedApiUser.getPassword();
			String passwordHash = PasswordHandler.getPasswordHash(originalPassword);
			String passwordSalt = PasswordHandler.getSaltFromHashedAndSaltedPassword(passwordHash);
			apiUserDao.updateApiUser(username, updatedApiUser.getName(), passwordHash, passwordSalt, updatedApiUser.getRoleId(), updatedApiUser.getTokenValue());
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException | CheckedIllegalArgumentException e) {
			
			e.printStackTrace();
		}
	}
	
	public void deleteApiUser(String username) {
		
		apiUserDao.deleteApiUser(username);
	}
}