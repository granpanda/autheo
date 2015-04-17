package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.infrastructure.exceptions.ExceptionUtils;
import gp.e3.autheo.authentication.persistence.daos.UserDAO;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserBusiness {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserBusiness.class);

	private final BasicDataSource dataSource;
	private final UserDAO userDao;

	public UserBusiness(BasicDataSource dataSource, UserDAO userDao) {

		this.dataSource = dataSource;
		this.userDao = userDao;
	}

	public User createUser(User newUser) {

		User createdUser = null;

		try (Connection dbConnection = dataSource.getConnection()) {

			String originalPassword = newUser.getPassword();
			String passwordHash = PasswordHandler.getPasswordHash(originalPassword);
			String passwordSalt = PasswordHandler.getSaltFromHashedAndSaltedPassword(passwordHash);

			createdUser = userDao.createUser(dbConnection, newUser, passwordHash, passwordSalt);

		} catch (SQLException e) {

			LOGGER.error("createUser: check the user was not already created.", e);
			ExceptionUtils.throwIllegalStateException(e);

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			
			LOGGER.error("createUser", e);
			ExceptionUtils.throwIllegalStateException(e);
			
		}

		return createdUser;
	}

	public boolean authenticateUser(String username, String password) {

		boolean isAuthenticated = false;

		try (Connection dbConnection = dataSource.getConnection()) {

			String passwordHashFromDb = userDao.getPasswordByUsername(dbConnection, username);
			isAuthenticated = PasswordHandler.validatePassword(password, passwordHashFromDb);				

		} catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {

			LOGGER.error("authenticateUser", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return isAuthenticated;
	}

	public User getUserByUsername(String username) {
		
		User user = null;
		
		try (Connection dbConnection = dataSource.getConnection()) {
			
			user = userDao.getUserByUsername(dbConnection, username);
			
		} catch (SQLException e) {
			
			LOGGER.error("getUserByUsername", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return user;
	}

	public List<User> getAllUsers() {

		List<User> usersList = new ArrayList<User>();
		
		try (Connection dbConnection = dataSource.getConnection()) {
			
			usersList = userDao.getAllUsers(dbConnection);
			
		} catch (SQLException e) {
			
			LOGGER.error("getAllUsers", e);
			ExceptionUtils.throwIllegalStateException(e);
		}
		
		return usersList;
	}

	public boolean updateUser(String username, User updatedUser) {

		boolean userWasUpdated = false;
		
		try (Connection dbConnection = dataSource.getConnection()) {
			
			userWasUpdated = userDao.updateUser(dbConnection, username, updatedUser.getName(), updatedUser.getPassword());
			
		} catch (SQLException e) {
			
			LOGGER.error("updateUser", e);
			ExceptionUtils.throwIllegalStateException(e);
		}
		
		return userWasUpdated;
	}

	public boolean deleteUser(String username) {

		boolean userWasDeleted = false;
		
		try (Connection dbConnection = dataSource.getConnection()) {
			
			userWasDeleted = userDao.deleteUser(dbConnection, username);
			
		} catch (SQLException e) {
			
			LOGGER.error("deleteUser", e);
			ExceptionUtils.throwIllegalStateException(e);
		}
		
		return userWasDeleted;
	}
}