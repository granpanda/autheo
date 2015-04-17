package gp.e3.autheo.authentication.persistence.daos;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.infrastructure.exceptions.ExceptionUtils;
import gp.e3.autheo.authentication.persistence.mappers.UserMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDAO.class);

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

	public void createUsersTableIfNotExists(Connection dbConnection) {

		try (PreparedStatement prepareStatement = dbConnection.prepareStatement(CREATE_USERS_TABLE_IF_NOT_EXISTS)) {

			prepareStatement.executeUpdate();

		} catch (SQLException e) {

			LOGGER.error("createUsersTableIfNotExists", e);
			ExceptionUtils.throwIllegalStateException(e);
		}
	}

	public int countUsersTable(Connection dbConnection) throws SQLException {

		int tableRows = 0;
		PreparedStatement preparedStatement = dbConnection.prepareStatement(COUNT_USERS_TABLE);
		ResultSet resultSet = preparedStatement.executeQuery();

		if (resultSet.next()) {
			tableRows = resultSet.getInt(1);
		}

		resultSet.close();
		preparedStatement.close();

		return tableRows;
	}

	public User createUser(Connection dbConnection, User user, String passwordHash, String passwordSalt) throws SQLException {

		String createUserSQL = "INSERT INTO users (name, username, password, salt, api_client, organization_id, role_id) VALUES (?, ?, ?, ?, ?, ?, ?);";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(createUserSQL);
		prepareStatement.setString(1, user.getName());
		prepareStatement.setString(2, user.getUsername());
		prepareStatement.setString(3, passwordHash);
		prepareStatement.setString(4, passwordSalt);
		prepareStatement.setBoolean(5, user.isApiClient());
		prepareStatement.setString(6, user.getOrganizationId());
		prepareStatement.setString(7, user.getRoleId());

		prepareStatement.executeUpdate();
		prepareStatement.close();

		return user;
	}

	public User getUserByUsername(Connection dbConnection, String username) throws SQLException {

		String getUserByUsernameSQL = "SELECT * FROM users WHERE username = ?;";
		PreparedStatement prepareStatement = dbConnection.prepareStatement(getUserByUsernameSQL);
		prepareStatement.setString(1, username);

		ResultSet resultSet = prepareStatement.executeQuery();
		User user = UserMapper.getSingleUserFromResultSet(resultSet);

		resultSet.close();
		prepareStatement.close();

		return user;
	}

	public List<User> getAllUsers(Connection dbConnection) throws SQLException {

		String getAllUsersSQL = "SELECT * FROM users;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(getAllUsersSQL);
		ResultSet resultSet = prepareStatement.executeQuery();
		List<User> allUsers = UserMapper.getMultipleUsersFromResultSet(resultSet);

		resultSet.close();
		prepareStatement.close();

		return allUsers;
	}

	public String getPasswordByUsername(Connection dbConnection, String username) throws SQLException {

		String password = "";
		String getPasswordByUsernameSQL = "SELECT password FROM users WHERE username = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(getPasswordByUsernameSQL);
		prepareStatement.setString(1, username);

		ResultSet resultSet = prepareStatement.executeQuery();

		if (resultSet.next()) {
			password = resultSet.getString(UserDAO.PASSWORD_FIELD);
		}

		resultSet.close();
		prepareStatement.close();

		return password;
	}

	public boolean updateUser(Connection dbConnection, String username, String updatedName, String updatedPassword) throws SQLException {

		String updateUserSQL = "UPDATE users SET name = ?, password = ? WHERE username = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(updateUserSQL);
		prepareStatement.setString(1, updatedName);
		prepareStatement.setString(2, updatedPassword);
		prepareStatement.setString(3, username);

		int affectedRows = prepareStatement.executeUpdate();
		boolean userWasUpdated = (affectedRows == 1);
		prepareStatement.close();

		return userWasUpdated;
	}

	public boolean deleteUser(Connection dbConnection, String username) throws SQLException {

		String deleteUserSQL = "DELETE FROM users WHERE username = ?;";
		PreparedStatement prepareStatement = dbConnection.prepareStatement(deleteUserSQL);
		prepareStatement.setString(1, username);

		int affectedRows = prepareStatement.executeUpdate();
		boolean userWasDeleted = (affectedRows == 1);
		prepareStatement.close();

		return userWasDeleted;
	}
}