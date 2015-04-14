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
import org.apache.commons.dbutils.DbUtils;
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
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();

			String originalPassword = newUser.getPassword();
			String passwordHash = PasswordHandler.getPasswordHash(originalPassword);
			String passwordSalt = PasswordHandler.getSaltFromHashedAndSaltedPassword(passwordHash);

			createdUser = userDao.createUser(dbConnection, newUser, passwordHash, passwordSalt);

		} catch (SQLException e) {

			LOGGER.error("createUser", e);
			throw new IllegalArgumentException("The given user is already created or is not valid");

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
			
			LOGGER.error("createUser", e);
			ExceptionUtils.throwIllegalStateException(e);
			
		} finally {

			DbUtils.closeQuietly(dbConnection);
		}

		return createdUser;
	}

	public boolean authenticateUser(String username, String password) {

		boolean isAuthenticated = false;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			String passwordHashFromDb = userDao.getPasswordByUsername(dbConnection, username);
			isAuthenticated = PasswordHandler.validatePassword(password, passwordHashFromDb);				

		} catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {

			LOGGER.error("authenticateUser", e);
			ExceptionUtils.throwIllegalStateException(e);

		} finally {

			DbUtils.closeQuietly(dbConnection);
		}

		return isAuthenticated;
	}

	public User getUserByUsername(String username) {
		
		User user = null;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			user = userDao.getUserByUsername(dbConnection, username);
			
		} catch (SQLException e) {
			
			LOGGER.error("getUserByUsername", e);
			ExceptionUtils.throwIllegalStateException(e);
			
		} finally {
			
			DbUtils.closeQuietly(dbConnection);
		}

		return user;
	}

	public List<User> getAllUsers() {

		List<User> usersList = new ArrayList<User>();
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			usersList = userDao.getAllUsers(dbConnection);
			
		} catch (SQLException e) {
			
			LOGGER.error("getAllUsers", e);
			ExceptionUtils.throwIllegalStateException(e);
			
		} finally {
			
			DbUtils.closeQuietly(dbConnection);
		}
		
		return usersList;
	}

	public boolean updateUser(String username, User updatedUser) {

		boolean userWasUpdated = false;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			userWasUpdated = userDao.updateUser(dbConnection, username, updatedUser.getName(), updatedUser.getPassword());
			
		} catch (SQLException e) {
			
			LOGGER.error("updateUser", e);
			ExceptionUtils.throwIllegalStateException(e);
			
		} finally {
			
			DbUtils.closeQuietly(dbConnection);
		}
		
		return userWasUpdated;
	}

	public boolean deleteUser(String username) {

		boolean userWasDeleted = false;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			userWasDeleted = userDao.deleteUser(dbConnection, username);
			
		} catch (SQLException e) {
			
			LOGGER.error("deleteUser", e);
			ExceptionUtils.throwIllegalStateException(e);
			
		} finally {
			
			DbUtils.closeQuietly(dbConnection);
		}
		
		return userWasDeleted;
	}
}