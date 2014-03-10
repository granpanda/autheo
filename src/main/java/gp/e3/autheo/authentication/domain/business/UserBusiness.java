package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.persistence.daos.IUserDAO;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.security.sasl.AuthenticationException;

public class UserBusiness {
	
	private final IUserDAO userDao;

	public UserBusiness(IUserDAO userDao) {
		
		this.userDao = userDao;
		this.userDao.createUsersTableIfNotExists();
	}
	
	public User createUser(User newUser) throws DuplicateIdException {
		
		try {
			
			String originalPassword = newUser.getPassword();
			String passwordHash = PasswordHandler.getPasswordHash(originalPassword);
			String passwordSalt = PasswordHandler.getSaltFromHashedAndSaltedPassword(passwordHash);
			
			userDao.createUser(newUser.getName(), newUser.getUsername(), passwordHash, passwordSalt);
			
		} catch (Exception e) {
			
			String errorMessage = "The user with username: " + newUser.getUsername() + " is already registered.";
			throw new DuplicateIdException(errorMessage);
		}
		
		return newUser;
	}
	
	public boolean authenticateUser(String username, String password) throws AuthenticationException {
		
		boolean isAuthenticated = false;
		String errorMessage = "The user credentials are not valid.";
		
		String passwordHashFromDb = userDao.getPasswordByUsername(username);
		
		if (passwordHashFromDb != null) {
			
			try {
				
				isAuthenticated = PasswordHandler.validatePassword(password, passwordHashFromDb);
				
			} catch (NoSuchAlgorithmException e) {
				throw new AuthenticationException(errorMessage);
			} catch (InvalidKeySpecException e) {
				throw new AuthenticationException(errorMessage);
			}
			
		} else {
			
			throw new AuthenticationException(errorMessage);
		}
		
		return isAuthenticated;
	}
	
	public User getUserByUsername(String username) {
		
		return userDao.getUserByUsername(username);
	}
	
	public List<User> getAllUsers() {
		
		return userDao.getAllUsers();
	}
	
	public void updateUser(String username, User updatedUser) {
		
		userDao.updateUser(username, updatedUser.getName(), updatedUser.getPassword());
	}
	
	public void deleteUser(String username) {
		
		userDao.deleteUser(username);
	}
}