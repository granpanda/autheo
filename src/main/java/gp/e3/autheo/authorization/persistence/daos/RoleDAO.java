package gp.e3.autheo.authorization.persistence.daos;

import gp.e3.autheo.authentication.infrastructure.exceptions.ExceptionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoleDAO.class);

	//------------------------------------------------------------------------------------------------------
	// Database column names constants
	//------------------------------------------------------------------------------------------------------

	public static final String USER_USERNAME_FIELD = "username";
	public static final String ROLE_ROLE_NAME_FIELD = "role_name";

	public static final String NAME_FIELD = "name";

	//------------------------------------------------------------------------------------------------------
	// SQL statements
	//------------------------------------------------------------------------------------------------------

	public static final String CREATE_ROLES_TABLE_IF_NOT_EXISTS = 
			"CREATE TABLE IF NOT EXISTS roles (name VARCHAR(32) PRIMARY KEY);";

	public static final String CREATE_ROLES_AND_PERMISSIONS_TABLE_IF_NOT_EXISTS = 
			"CREATE TABLE IF NOT EXISTS roles_permissions (role_name VARCHAR(32), permission_id INT, PRIMARY KEY (role_name, permission_id));";

	public static final String CREATE_ROLES_AND_USERS_TABLE_IF_NOT_EXISTS = 
			"CREATE TABLE IF NOT EXISTS roles_users (username VARCHAR(32) PRIMARY KEY, role_name VARCHAR(32));";

	public static final String COUNT_ROLES_TABLE = "SELECT COUNT(*) FROM roles;";

	public static final String COUNT_ROLE_PERMISSIONS_TABLE = "SELECT COUNT(*) FROM roles_permissions;";

	public static final String COUNT_ROLE_USERS_TABLE = "SELECT COUNT(*) FROM roles_users;";

	public static final String CREATE_ROLE = "INSERT INTO roles (name) VALUES (:name);";

	public static final String GET_ALL_ROLES_NAMES = "SELECT name FROM roles;";

	public static final String DELETE_ROLE = "DELETE FROM roles WHERE name = :name;";

	public static final String ADD_USER_TO_ROLE = "INSERT INTO roles_users (username, role_name) VALUES (:username, :role_name);";

	public static final String REMOVE_USER_FROM_ROLE = "DELETE FROM roles_users WHERE username = :username;";

	public static final String REMOVE_ALL_USERS_FROM_ROLE = "DELETE FROM roles_users WHERE role_name = :role_name;";

	//------------------------------------------------------------------------------------------------------
	// Database operations
	//------------------------------------------------------------------------------------------------------

	public boolean createRolesTableIfNotExists(Connection dbConnection) {

		boolean rolesTableWasCreated = false;

		try (PreparedStatement prepareStatement = dbConnection.prepareStatement(CREATE_ROLES_TABLE_IF_NOT_EXISTS)) {

			int result = prepareStatement.executeUpdate();
			rolesTableWasCreated = (result != 0);

		} catch (SQLException e) {

			LOGGER.error("createRolesTableIfNotExists", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return rolesTableWasCreated;
	}

	public boolean createRolesAndPermissionsTableIfNotExists(Connection dbConnection) {

		boolean rolesAndPermissionsTableWasCreated = false;

		try (PreparedStatement prepareStatement = dbConnection.prepareStatement(CREATE_ROLES_AND_PERMISSIONS_TABLE_IF_NOT_EXISTS)) {

			int result = prepareStatement.executeUpdate();
			rolesAndPermissionsTableWasCreated = (result != 0);

		} catch (SQLException e) {

			LOGGER.error("createRolesAndPermissionsTableIfNotExists", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return rolesAndPermissionsTableWasCreated;
	}

	public boolean createRolesAndUsersTableIfNotExists(Connection dbConnection) {

		boolean rolesAndUsersTableWasCreated = false;

		try (PreparedStatement prepareStatement = dbConnection.prepareStatement(CREATE_ROLES_AND_USERS_TABLE_IF_NOT_EXISTS)) {

			int result = prepareStatement.executeUpdate();
			rolesAndUsersTableWasCreated = (result != 0);

		} catch (SQLException e) {

			LOGGER.error("createRolesAndUsersTableIfNotExists", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return rolesAndUsersTableWasCreated;
	}

	private int getTableRowsFromResultSet(ResultSet resultSet) throws SQLException {

		int tableRows = 0;

		if (resultSet.next()) {
			tableRows = resultSet.getInt(1);
		}

		return tableRows;
	}

	public int countRolesTable(Connection dbConnection) throws SQLException {

		PreparedStatement prepareStatement = dbConnection.prepareStatement(COUNT_ROLES_TABLE);
		ResultSet resultSet = prepareStatement.executeQuery();
		int tableRows = getTableRowsFromResultSet(resultSet);

		resultSet.close();
		prepareStatement.close();

		return tableRows;
	}

	public int countRolePermissionsTable(Connection dbConnection) throws SQLException {

		PreparedStatement prepareStatement = dbConnection.prepareStatement(COUNT_ROLE_PERMISSIONS_TABLE);
		ResultSet resultSet = prepareStatement.executeQuery();
		int tableRows = getTableRowsFromResultSet(resultSet);

		resultSet.close();
		prepareStatement.close();

		return tableRows;
	}

	public int countRoleUsersTable(Connection dbConnection) throws SQLException {

		PreparedStatement prepareStatement = dbConnection.prepareStatement(COUNT_ROLE_USERS_TABLE);
		ResultSet resultSet = prepareStatement.executeQuery();
		int tableRows = getTableRowsFromResultSet(resultSet);

		resultSet.close();
		prepareStatement.close();

		return tableRows;
	}

	public int createRole(Connection dbConnection, String roleName) throws SQLException {

		String createRoleSQL = "INSERT INTO roles (name) VALUES (?);";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(createRoleSQL);
		prepareStatement.setString(1, roleName);

		int affectedRows = prepareStatement.executeUpdate();
		prepareStatement.close();

		return affectedRows;
	}

	public List<String> getAllRolesNames(Connection dbConnection) throws SQLException {

		List<String> roleNamesList = new ArrayList<String>();
		String getAllRolesNamesSQL = "SELECT name FROM roles;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(getAllRolesNamesSQL);
		ResultSet resultSet = prepareStatement.executeQuery();

		while (resultSet.next()) {

			String roleName = resultSet.getString(1);
			roleNamesList.add(roleName);
		}

		resultSet.close();
		prepareStatement.close();

		return roleNamesList;
	}

	public int deleteRole(Connection dbConnection, String roleName) throws SQLException {

		String deleteRoleSQL = "DELETE FROM roles WHERE name = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(deleteRoleSQL);
		prepareStatement.setString(1, roleName);
		int affectedRows = prepareStatement.executeUpdate();
		prepareStatement.close();

		return affectedRows;
	}

	public int addUserToRole(Connection dbConnection, String username, String roleName) throws SQLException {

		String addUserToRoleSQL = "INSERT INTO roles_users (username, role_name) VALUES (?, ?);";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(addUserToRoleSQL);
		prepareStatement.setString(1, username);
		prepareStatement.setString(2, roleName);

		int affectedRows = prepareStatement.executeUpdate();
		prepareStatement.close();

		return affectedRows;
	}

	public int removeUserFromRole(Connection dbConnection, String username) throws SQLException {

		String removeUserFromRoleSQL = "DELETE FROM roles_users WHERE username = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(removeUserFromRoleSQL);
		prepareStatement.setString(1, username);

		int affectedRows = prepareStatement.executeUpdate();
		prepareStatement.close();

		return affectedRows;
	}

	public boolean removeAllUsersFromRole(Connection dbConnection, String roleName) throws SQLException {

		boolean allUsersWereRemovedFromRole = false;
		String removeAllUsersFromRoleSQL = "DELETE FROM roles_users WHERE role_name = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(removeAllUsersFromRoleSQL);
		prepareStatement.setString(1, roleName);

		int affectedRows = prepareStatement.executeUpdate();
		allUsersWereRemovedFromRole = (affectedRows != 0);
		prepareStatement.close();

		return allUsersWereRemovedFromRole;
	}
}