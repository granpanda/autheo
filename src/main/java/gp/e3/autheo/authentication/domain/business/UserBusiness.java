package gp.e3.autheo.authentication.domain.business;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.infrastructure.datastructures.Tuple;
import gp.e3.autheo.authentication.infrastructure.utils.SqlUtils;
import gp.e3.autheo.authentication.persistence.daos.UserDAO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;

public class UserBusiness {

	private final UserDAO userDao;
	private final BasicDataSource dataSource;

	public UserBusiness(BasicDataSource dataSource, UserDAO userDao) {

		this.dataSource = dataSource;
		this.userDao = userDao;
	}

	public Tuple createUser(User newUser) {

		Tuple answerTuple = null;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();

			String originalPassword = newUser.getPassword();
			String passwordHash = PasswordHandler.getPasswordHash(originalPassword);
			String passwordSalt = PasswordHandler.getSaltFromHashedAndSaltedPassword(passwordHash);

			answerTuple = userDao.createUser(dbConnection, newUser, passwordHash, passwordSalt);

		} catch (Exception e) {

			e.printStackTrace();
			answerTuple = new Tuple(e.getMessage());

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return answerTuple;
	}

	public boolean authenticateUser(String username, String password) {

		boolean isAuthenticated = false;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			String passwordHashFromDb = userDao.getPasswordByUsername(dbConnection, username);

			if (passwordHashFromDb != null) {
				isAuthenticated = PasswordHandler.validatePassword(password, passwordHashFromDb);

			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return isAuthenticated;
	}

	public User getUserByUsername(String username) {
		
		User user = null;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			user = userDao.getUserByUsername(dbConnection, username);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}

		return user;
	}

	public List<User> getAllUsers() {

		List<User> usersList = new ArrayList<User>();
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			usersList = userDao.getAllUsers(dbConnection);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return usersList;
	}

	public boolean updateUser(String username, User updatedUser) {

		boolean userWasUpdated = false;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			int rowsAffected = userDao.updateUser(dbConnection, username, updatedUser.getName(), updatedUser.getPassword());
			userWasUpdated = (rowsAffected == 1);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return userWasUpdated;
	}

	public boolean deleteUser(String username) {

		boolean userWasDeleted = false;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			int affectedRows = userDao.deleteUser(dbConnection, username);
			userWasDeleted = (affectedRows == 1);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return userWasDeleted;
	}
}