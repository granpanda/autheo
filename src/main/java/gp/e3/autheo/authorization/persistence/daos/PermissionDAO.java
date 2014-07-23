package gp.e3.autheo.authorization.persistence.daos;

import gp.e3.autheo.authentication.infrastructure.utils.SqlUtils;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.persistence.mappers.PermissionMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.SqlQuery;

public class PermissionDAO {

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

	public int createPermissionsTable(Connection dbConnection) {

		int affectedRows = 0;

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(CREATE_PERMISSIONS_TABLE_IF_NOT_EXISTS);
			affectedRows = prepareStatement.executeUpdate();
			prepareStatement.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return affectedRows;
	}

	public boolean createPermissionsUniqueIndex(Connection dbConnection) {

		boolean indexWasCreated = false;

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(CREATE_PERMISSIONS_UNIQUE_INDEX);
			int affectedRows = prepareStatement.executeUpdate();
			indexWasCreated = (affectedRows != 0);
			prepareStatement.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return indexWasCreated;
	}

	public int countPermissionsTable(Connection dbConnection) {

		int tableRows = 0;

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(COUNT_PERMISSIONS_TABLE);
			ResultSet resultSet = prepareStatement.executeQuery();

			if (resultSet.next()) {
				tableRows = resultSet.getInt(1);
			}

			resultSet.close();
			prepareStatement.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return tableRows;
	}

	public int countRolePermissionsTable(Connection dbConnection) {

		int tableRows = 0;

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(COUNT_ROLE_PERMISSIONS_TABLE);
			ResultSet resultSet = prepareStatement.executeQuery();

			if (resultSet.next()) {
				tableRows = resultSet.getInt(1);
			}

			resultSet.close();
			prepareStatement.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return tableRows;
	}

	public long createPermission(Connection dbConnection, Permission permission) {

		long permissionId = 0;
		String createPermissionSQL = "INSERT INTO permissions (name, http_verb, url) VALUES (?, ?, ?);";

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(createPermissionSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			prepareStatement.setString(1, permission.getName());
			prepareStatement.setString(2, permission.getHttpVerb());
			prepareStatement.setString(3, permission.getUrl());

			prepareStatement.executeUpdate();
			permissionId = SqlUtils.getGeneratedIdFromResultSet(prepareStatement.getGeneratedKeys());
			prepareStatement.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return permissionId;
	}

	private int getTotalResultFromBatchResult(int[] batchResult) {

		int totalBatchResult = 0;

		for (int result : batchResult) {
			totalBatchResult += result;
		}

		return totalBatchResult;
	}
	
	public int associateAllPermissionsToRole(Connection dbConnection, String roleName, List<Integer> permissionIdList) {

		int affectedRows = 0;
		String associateAllPermissionsToRoleSQL = "INSERT INTO roles_permissions (role_name, permission_id) VALUES (?, ?);";

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(associateAllPermissionsToRoleSQL);

			for (Integer permissionId : permissionIdList) {

				prepareStatement.setString(1, roleName);
				prepareStatement.setInt(2, permissionId);
				prepareStatement.addBatch();
			}

			int[] batchResult = prepareStatement.executeBatch();
			affectedRows = getTotalResultFromBatchResult(batchResult);
			prepareStatement.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		return affectedRows;
	}
	
	public int disassociateAllPermissionsFromRole(Connection dbConnection, String roleName) {
		
		int affectedRows = 0;
		String disassociateAllPermissionsFromRoleSQL = "DELETE FROM roles_permissions WHERE role_name = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(disassociateAllPermissionsFromRoleSQL);
			prepareStatement.setString(1, roleName);
			
			affectedRows = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return affectedRows;
	}
	
	public int disassociatePermissionFromAllRoles(Connection dbConnection, int permissionId) {
		
		int affectedRows = 0;
		String disassociatePermissionFromAllRolesSQL = "DELETE FROM roles_permissions WHERE permission_id = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(disassociatePermissionFromAllRolesSQL);
			prepareStatement.setInt(1, permissionId);
			
			affectedRows = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return affectedRows;
	}
	
	public Permission getPermissionById(Connection dbConnection, long permissionId) {
		
		Permission permission = null;
		String getPermissionByIdSQL = "SELECT * FROM permissions WHERE id = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getPermissionByIdSQL);
			prepareStatement.setLong(1, permissionId);
			
			ResultSet resultSet = prepareStatement.executeQuery();
			permission = PermissionMapper.getSinglePermission(resultSet);
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return permission;
	}

	@SqlQuery(GET_PERMISSION_BY_HTTP_VERB_AND_URL)
	public Permission getPermissionByHttpVerbAndUrl(Connection dbConnection, String httpVerb, String url) {
		
		Permission permission = null;
		String getPermissionByHttpVerbAndUrlSQL = "SELECT * FROM permissions WHERE http_verb = ? AND url = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getPermissionByHttpVerbAndUrlSQL);
			prepareStatement.setString(1, httpVerb);
			prepareStatement.setString(2, url);
			
			ResultSet resultSet = prepareStatement.executeQuery();
			permission = PermissionMapper.getSinglePermission(resultSet);
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return permission;
	}
	
	public List<Permission> getAllPermissions(Connection dbConnection) {
		
		List<Permission> allPermissions = new ArrayList<Permission>();
		String getAllPermissionsSQL = "SELECT * FROM permissions;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getAllPermissionsSQL);
			ResultSet resultSet = prepareStatement.executeQuery();
			
			allPermissions = PermissionMapper.getMultiplePermissions(resultSet);
			resultSet.close();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return allPermissions;
	}
	
	public List<Permission> getAllPermissionsOfAGivenRole(Connection dbConnection, String roleName) {
		
		List<Permission> permissionsOfARole = new ArrayList<Permission>();
		String getAllPermissionsOfAGivenRoleSQL = "SELECT * FROM permissions LEFT JOIN roles_permissions ON permissions.id = roles_permissions.permission_id WHERE role_name = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getAllPermissionsOfAGivenRoleSQL);
			prepareStatement.setString(1, roleName);
			
			ResultSet resultSet = prepareStatement.executeQuery();
			permissionsOfARole = PermissionMapper.getMultiplePermissions(resultSet);
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return permissionsOfARole;
	}
	
	public int deletePermission(Connection dbConnection, long permissionId) {
		
		int affectedRows = 0;
		String deletePermissionSQL = "DELETE FROM permissions WHERE id = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(deletePermissionSQL);
			prepareStatement.setLong(1, permissionId);
			
			affectedRows = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return affectedRows;
	}
}