package gp.e3.autheo.authentication.persistence.daos;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.infrastructure.datastructures.Tuple;
import gp.e3.autheo.authentication.persistence.mappers.UserMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
	
	public static final String NAME_FIELD = "name";
	public static final String USERNAME_FIELD = "username";
	public static final String PASSWORD_FIELD = "password";
	public static final String SALT_FIELD = "salt";
	public static final String IS_API_CLIENT_FIELD = "api_client";
	public static final String ORGANIZATION_ID_FIELD = "organization_id";
	public static final String ROLE_ID_FIELD = "role_id";
	
	public static final String CREATE_USERS_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS users (name varchar(32), username varchar(32) primary key, "
			+ "password varchar(256), salt varchar(256), api_client TINYINT(1), organization_id varchar(32), role_id varchar(32));";
	
	public static final String COUNT_USERS_TABLE = "SELECT COUNT(*) FROM users;";
	
	public static final String INSERT_USER = "INSERT INTO users (name, username, password, salt, api_client, organization_id, role_id) VALUES (:name, :username, :password, "
			+ ":salt, :api_client, :organization_id, :role_id);";
	
	public static final String GET_USER_BY_USERNAME = "SELECT * FROM users WHERE username = :username;";
	public static final String GET_PASSWORD_BY_USERNAME = "SELECT password FROM users WHERE username = :username;";
	public static final String GET_ALL_USERS = "SELECT * FROM users;";
	public static final String UPDATE_USER_BY_USERNAME = "UPDATE users SET name = :name, password = :password WHERE username = :username;";
	public static final String DELETE_USER_BY_USERNAME = "DELETE FROM users WHERE username = :username;";
	
	public int createUsersTableIfNotExists(Connection dbConnection) {
		
		int result = 0;
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(CREATE_USERS_TABLE_IF_NOT_EXISTS);
			result = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int countUsersTable(Connection dbConnection) {
		
		int tableRows = 0;
		
		try {
			
			PreparedStatement preparedStatement = dbConnection.prepareStatement(COUNT_USERS_TABLE);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				tableRows = resultSet.getInt(1);
			}
			
			resultSet.close();
			preparedStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return tableRows;
	}
	
	public Tuple createUser(Connection dbConnection, User user, String passwordHash, String passwordSalt) {
		
		Tuple tuple = null;
		String createUserSQL = "INSERT INTO users (name, username, password, salt, api_client, organization_id, role_id) VALUES (?, ?, ?, ?, ?, ?, ?);";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(createUserSQL);
			prepareStatement.setString(1, user.getName());
			prepareStatement.setString(2, user.getUsername());
			prepareStatement.setString(3, passwordHash);
			prepareStatement.setString(4, passwordSalt);
			prepareStatement.setBoolean(5, user.isApiClient());
			prepareStatement.setString(6, user.getOrganizationId());
			prepareStatement.setString(7, user.getRoleId());
			
			int affectedRows = prepareStatement.executeUpdate();
			prepareStatement.close();
			
			boolean userWasCreated = (affectedRows == 1);
			
			if (userWasCreated) {
				
				tuple = new Tuple(userWasCreated);
				
			} else {
				
				String errorMessage = "Please check that none of the user fields is null.";
				tuple = new Tuple(errorMessage);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			tuple = new Tuple(e.getMessage());
		}
		
		return tuple;
	};
	
	public User getUserByUsername(Connection dbConnection, String username) {
		
		User user = null;
		String getUserByUsernameSQL = "SELECT * FROM users WHERE username = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getUserByUsernameSQL);
			prepareStatement.setString(1, username);
			
			ResultSet resultSet = prepareStatement.executeQuery();
			user = UserMapper.getSingleUserFromResultSet(resultSet);
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return user;
	}
	
	public List<User> getAllUsers(Connection dbConnection) {
		
		List<User> allUsers = new ArrayList<User>();
		String getAllUsersSQL = "SELECT * FROM users;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getAllUsersSQL);
			ResultSet resultSet = prepareStatement.executeQuery();
			allUsers = UserMapper.getMultipleUsersFromResultSet(resultSet);
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return allUsers;
	}
	
	public String getPasswordByUsername(Connection dbConnection, String username) {
		
		String password = "";
		String getPasswordByUsernameSQL = "SELECT password FROM users WHERE username = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getPasswordByUsernameSQL);
			prepareStatement.setString(1, username);
			
			ResultSet resultSet = prepareStatement.executeQuery();
			
			if (resultSet.next()) {
				password = resultSet.getString(UserDAO.PASSWORD_FIELD);
			}
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return password;
	}
	
	public int updateUser(Connection dbConnection, String username, String updatedName, String updatedPassword) {
		
		int affectedRows = 0;
		String updateUserSQL = "UPDATE users SET name = ?, password = ? WHERE username = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(updateUserSQL);
			prepareStatement.setString(1, updatedName);
			prepareStatement.setString(2, updatedPassword);
			prepareStatement.setString(3, username);
			
			affectedRows = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return affectedRows;
	}
	
	public int deleteUser(Connection dbConnection, String username) {
		
		int affectedRows = 0;
		String deleteUserSQL = "DELETE FROM users WHERE username = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(deleteUserSQL);
			prepareStatement.setString(1, username);
			
			affectedRows = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return affectedRows;
	}
}