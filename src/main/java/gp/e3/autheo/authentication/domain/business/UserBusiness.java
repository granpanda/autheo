package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.persistence.daos.IUserDAO;
import gp.e3.autheo.authentication.persistence.exceptions.DuplicateIdException;

import java.util.List;

public class UserBusiness {
	
	private final IUserDAO userDao;

	public UserBusiness(IUserDAO userDao) {
		
		this.userDao = userDao;
		this.userDao.createUsersTableIfNotExists();
	}
	
	public User createUser(User newUser) throws DuplicateIdException {
		
		try {
			
			userDao.createUser(newUser.getName(), newUser.getUsername(), newUser.getPassword());
			
		} catch (Exception e) {
			
			String errorMessage = "The user with username: " + newUser.getUsername() + " is already registered.";
			throw new DuplicateIdException(errorMessage);
		}
		
		return newUser;
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