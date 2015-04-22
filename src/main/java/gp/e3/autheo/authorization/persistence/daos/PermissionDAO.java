package gp.e3.autheo.authorization.persistence.daos;

import gp.e3.autheo.authentication.infrastructure.exceptions.ExceptionUtils;
import gp.e3.autheo.authentication.infrastructure.utils.SqlUtils;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.persistence.mappers.PermissionMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionDAO.class);

	//------------------------------------------------------------------------------------------------------
	// Database column names constants
	//------------------------------------------------------------------------------------------------------

	public static final String ROLE_NAME_FIELD = "role_name";
	public static final String PERMISSION_ID_FIELD = "permission_id";

	public static final String ID_FIELD = "id";
	public static final String NAME_FIELD = "name";
	public static final String HTTP_VERB_FIELD = "http_verb";
	public static final String URL_FIELD = "url";

	//------------------------------------------------------------------------------------------------------
	// SQL statements
	//------------------------------------------------------------------------------------------------------

	public static final String CREATE_PERMISSIONS_TABLE_IF_NOT_EXISTS = 
			"CREATE TABLE IF NOT EXISTS permissions (id INT AUTO_INCREMENT, name VARCHAR(32), http_verb VARCHAR(32), url VARCHAR(256), PRIMARY KEY (id));";

	public static final String CREATE_PERMISSIONS_UNIQUE_INDEX =  
			"ALTER TABLE permissions ADD UNIQUE INDEX(http_verb, url);";

	public static final String COUNT_PERMISSIONS_TABLE = "SELECT COUNT(*) FROM permissions;";

	public static final String COUNT_ROLE_PERMISSIONS_TABLE = "SELECT COUNT(*) FROM roles_permissions;";

	public static final String CREATE_PERMISSION = 
			"INSERT INTO permissions (name, http_verb, url) VALUES (:name, :http_verb, :url);";

	public static final String ASSOCIATE_ALL_PERMISSIONS_TO_ROLE = 
			"INSERT INTO roles_permissions (role_name, permission_id) VALUES (:role_name, :permission_id);";

	public static final String DISASSOCIATE_ALL_PERMISSIONS_FROM_ROLE = 
			"DELETE FROM roles_permissions WHERE role_name = :role_name;";

	public static final String DISASSOCIATE_PERMISSION_FROM_ALL_ROLES = 
			"DELETE FROM roles_permissions WHERE permission_id = :permission_id;";

	public static final String GET_PERMISSION_BY_ID = "SELECT * FROM permissions WHERE id = :id;";

	public static final String GET_PERMISSION_BY_HTTP_VERB_AND_URL = 
			"SELECT * FROM permissions WHERE http_verb = :http_verb AND url = :url;";

	public static final String GET_ALL_PERMISSIONS = "SELECT * FROM permissions;";

	public static final String GET_ALL_PERMISSIONS_OF_A_GIVEN_ROLE = 
			"SELECT * FROM permissions LEFT JOIN roles_permissions ON permissions.id = roles_permissions.permission_id WHERE role_name = :role_name;";

	public static final String DELETE_PERMISSION = "DELETE FROM permissions WHERE id = :id;";

	//------------------------------------------------------------------------------------------------------
	// Database operations
	//------------------------------------------------------------------------------------------------------

	private void createPermissionsUniqueIndex(Connection dbConnection) {

	    try {
	        
	        PreparedStatement prepareStatement = dbConnection.prepareStatement(CREATE_PERMISSIONS_UNIQUE_INDEX);
	        prepareStatement.executeUpdate();
	        prepareStatement.close();
            
        } catch (SQLException e) {
            
            LOGGER.info("createPermissionsUniqueIndex: " + e.getMessage());
        }		
	}

	public void createPermissionsTable(Connection dbConnection) {

		try (PreparedStatement prepareStatement = dbConnection.prepareStatement(CREATE_PERMISSIONS_TABLE_IF_NOT_EXISTS)) {

			prepareStatement.executeUpdate();
			createPermissionsUniqueIndex(dbConnection);

		} catch (SQLException e) {

			LOGGER.error("createPermissionsTable", e);
			ExceptionUtils.throwIllegalStateException(e);
		}
	}

	public int countPermissionsTable(Connection dbConnection) throws SQLException {

		int tableRows = 0;

		PreparedStatement prepareStatement = dbConnection.prepareStatement(COUNT_PERMISSIONS_TABLE);
		ResultSet resultSet = prepareStatement.executeQuery();

		if (resultSet.next()) {
			tableRows = resultSet.getInt(1);
		}

		resultSet.close();
		prepareStatement.close();

		return tableRows;
	}

	public int countRolePermissionsTable(Connection dbConnection) throws SQLException {

		int tableRows = 0;

		PreparedStatement prepareStatement = dbConnection.prepareStatement(COUNT_ROLE_PERMISSIONS_TABLE);
		ResultSet resultSet = prepareStatement.executeQuery();

		if (resultSet.next()) {
			tableRows = resultSet.getInt(1);
		}

		resultSet.close();
		prepareStatement.close();

		return tableRows;
	}

	public long createPermission(Connection dbConnection, Permission permission) throws SQLException {

		long permissionId = 0;
		String createPermissionSQL = "INSERT INTO permissions (name, http_verb, url) VALUES (?, ?, ?);";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(createPermissionSQL, PreparedStatement.RETURN_GENERATED_KEYS);
		prepareStatement.setString(1, permission.getName());
		prepareStatement.setString(2, permission.getHttpVerb());
		prepareStatement.setString(3, permission.getUrl());

		int affectedRows = prepareStatement.executeUpdate();

		if (affectedRows > 0) {
			permissionId = SqlUtils.getGeneratedIdFromResultSet(prepareStatement.getGeneratedKeys());
		}

		prepareStatement.close();

		return permissionId;
	}

	private int getTotalResultFromBatchResult(int[] batchResult) {

		int totalBatchResult = 0;

		for (int result : batchResult) {
			totalBatchResult += result;
		}

		return totalBatchResult;
	}

	public int associateAllPermissionsToRole(Connection dbConnection, String roleName, List<Long> permissionIdList) throws SQLException {

		String associateAllPermissionsToRoleSQL = "INSERT INTO roles_permissions (role_name, permission_id) VALUES (?, ?);";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(associateAllPermissionsToRoleSQL);

		for (Long permissionId : permissionIdList) {

			prepareStatement.setString(1, roleName);
			prepareStatement.setLong(2, permissionId);
			prepareStatement.addBatch();
		}

		int[] batchResult = prepareStatement.executeBatch();
		int affectedRows = getTotalResultFromBatchResult(batchResult);
		prepareStatement.close();

		return affectedRows;
	}

	public int disassociateAllPermissionsFromRole(Connection dbConnection, String roleName) throws SQLException {

		String disassociateAllPermissionsFromRoleSQL = "DELETE FROM roles_permissions WHERE role_name = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(disassociateAllPermissionsFromRoleSQL);
		prepareStatement.setString(1, roleName);

		int affectedRows = prepareStatement.executeUpdate();
		prepareStatement.close();

		return affectedRows;
	}

	public int disassociatePermissionFromAllRoles(Connection dbConnection, long permissionId) throws SQLException {

		String disassociatePermissionFromAllRolesSQL = "DELETE FROM roles_permissions WHERE permission_id = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(disassociatePermissionFromAllRolesSQL);
		prepareStatement.setLong(1, permissionId);

		int affectedRows = prepareStatement.executeUpdate();
		prepareStatement.close();

		return affectedRows;
	}

	public Permission getPermissionById(Connection dbConnection, long permissionId) throws SQLException {

		String getPermissionByIdSQL = "SELECT * FROM permissions WHERE id = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(getPermissionByIdSQL);
		prepareStatement.setLong(1, permissionId);

		ResultSet resultSet = prepareStatement.executeQuery();
		Permission permission = PermissionMapper.getSinglePermission(resultSet);

		resultSet.close();
		prepareStatement.close();

		return permission;
	}

	public Permission getPermissionByHttpVerbAndUrl(Connection dbConnection, String httpVerb, String url) throws SQLException {

		String getPermissionByHttpVerbAndUrlSQL = "SELECT * FROM permissions WHERE http_verb = ? AND url = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(getPermissionByHttpVerbAndUrlSQL);
		prepareStatement.setString(1, httpVerb);
		prepareStatement.setString(2, url);

		ResultSet resultSet = prepareStatement.executeQuery();
		Permission permission = PermissionMapper.getSinglePermission(resultSet);

		resultSet.close();
		prepareStatement.close();

		return permission;
	}

	public List<Permission> getAllPermissions(Connection dbConnection) throws SQLException {

		String getAllPermissionsSQL = "SELECT * FROM permissions;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(getAllPermissionsSQL);
		ResultSet resultSet = prepareStatement.executeQuery();

		List<Permission> allPermissions = PermissionMapper.getMultiplePermissions(resultSet);
		resultSet.close();
		prepareStatement.close();

		return allPermissions;
	}

	public List<Permission> getAllPermissionsOfAGivenRole(Connection dbConnection, String roleName) throws SQLException {

		String getAllPermissionsOfAGivenRoleSQL = "SELECT * FROM permissions LEFT JOIN roles_permissions ON permissions.id = roles_permissions.permission_id WHERE role_name = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(getAllPermissionsOfAGivenRoleSQL);
		prepareStatement.setString(1, roleName);

		ResultSet resultSet = prepareStatement.executeQuery();
		List<Permission> permissionsOfARole = PermissionMapper.getMultiplePermissions(resultSet);

		resultSet.close();
		prepareStatement.close();

		return permissionsOfARole;
	}

	public int deletePermission(Connection dbConnection, long permissionId) throws SQLException {

		String deletePermissionSQL = "DELETE FROM permissions WHERE id = ?;";

		PreparedStatement prepareStatement = dbConnection.prepareStatement(deletePermissionSQL);
		prepareStatement.setLong(1, permissionId);

		int affectedRows = prepareStatement.executeUpdate();
		prepareStatement.close();

		return affectedRows;
	}
}