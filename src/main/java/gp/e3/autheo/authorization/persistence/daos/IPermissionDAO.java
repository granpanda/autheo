package gp.e3.autheo.authorization.persistence.daos;

import gp.e3.autheo.authorization.domain.entities.Permission;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface IPermissionDAO {

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
			"CREATE TABLE IF NOT EXISTS permissions (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(32), http_verb VARCHAR(32), url VARCHAR(256), PRIMARY KEY (id));";
	
	public static final String CREATE_ROLES_AND_PERMISSIONS_TABLE_IF_NOT_EXISTS = 
			"CREATE TABLE IF NOT EXISTS roles_permissions (role_name VARCHAR(32), permission_id INT, PRIMARY KEY (role_name, permission_id));";

	public static final String CREATE_PERMISSION = 
			"INSERT INTO permissions (name, http_verb, url) VALUES (:name, :http_verb, :url)";
	
	public static final String ASSOCIATE_ALL_PERMISSIONS_TO_ROLE = 
			"INSERT INTO roles_permissions (role_name, permission_id) VALUES (:role_name, :permission_id);";
	
	public static final String DISASSOCIATE_ALL_PERMISSIONS_FROM_ROLE = 
			"DELETE FROM roles_permissions WHERE role_name = :role_name);";

	//------------------------------------------------------------------------------------------------------
	// Database operations
	//------------------------------------------------------------------------------------------------------

	@SqlUpdate(CREATE_PERMISSIONS_TABLE_IF_NOT_EXISTS)
	public void createPermissionsTable();

	@SqlUpdate(CREATE_PERMISSION)
	public void createPermission(@Bind(NAME_FIELD) String name, @Bind(HTTP_VERB_FIELD) String httpVerb, 
			@Bind(URL_FIELD) String url);

	@SqlUpdate(ASSOCIATE_ALL_PERMISSIONS_TO_ROLE)
	public int associateAllPermissionsToRole(@Bind(ROLE_NAME_FIELD) String roleName, 
			@Bind(PERMISSION_ID_FIELD) List<Integer> permissions);
	
	public int disassociatePermissionFromAllRoles(String permissionId);

	@SqlUpdate(DISASSOCIATE_ALL_PERMISSIONS_FROM_ROLE)
	public int disassociateAllPermissionsFromRole(@Bind(ROLE_NAME_FIELD) String roleName);

	public Permission getPermissionById(String permissionId);

	public Permission getPermissionByHttpVerbAndUrl(String httpVerb, String url);

	public List<Permission> getAllPermissions();

	public List<Permission> getAllPermissionsOfAGivenRole(String roleName);

	public int deletePermission(String permissionId);
}