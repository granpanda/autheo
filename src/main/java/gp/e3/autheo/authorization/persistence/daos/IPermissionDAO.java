package gp.e3.autheo.authorization.persistence.daos;

import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.persistence.mappers.PermissionMapper;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

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
			"CREATE TABLE IF NOT EXISTS permissions (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(32), http_verb VARCHAR(32), url VARCHAR(256), PRIMARY KEY (http_verb, url));";

	public static final String CREATE_PERMISSION = 
			"INSERT INTO permissions (name, http_verb, url) VALUES (:name, :http_verb, :url)";
	
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
			"SELECT * FROM permissions LEFT JOIN roles_permissions WHERE permissions.id = roles_permissions.permission_id AND role_name = :role_name";
	
	public static final String DELETE_PERMISSION = "DELETE FROM permissions WHERE id = :id";

	//------------------------------------------------------------------------------------------------------
	// Database operations
	//------------------------------------------------------------------------------------------------------

	@SqlUpdate(CREATE_PERMISSIONS_TABLE_IF_NOT_EXISTS)
	public void createPermissionsTable();

	@SqlUpdate(CREATE_PERMISSION)
	public void createPermission(@Bind(NAME_FIELD) String name, @Bind(HTTP_VERB_FIELD) String httpVerb, 
			@Bind(URL_FIELD) String url) throws Exception;

	@SqlBatch(ASSOCIATE_ALL_PERMISSIONS_TO_ROLE)
	public int associateAllPermissionsToRole(@Bind(ROLE_NAME_FIELD) String roleName, 
			@Bind(PERMISSION_ID_FIELD) List<Integer> permissions);

	@SqlUpdate(DISASSOCIATE_ALL_PERMISSIONS_FROM_ROLE)
	public int disassociateAllPermissionsFromRole(@Bind(ROLE_NAME_FIELD) String roleName);
	
	@SqlUpdate(DISASSOCIATE_PERMISSION_FROM_ALL_ROLES)
	public int disassociatePermissionFromAllRoles(@Bind(PERMISSION_ID_FIELD) String permissionId);

	@SqlQuery(GET_PERMISSION_BY_ID)
	@Mapper(PermissionMapper.class)
	public Permission getPermissionById(@Bind(ID_FIELD) String permissionId);

	@SqlQuery(GET_PERMISSION_BY_HTTP_VERB_AND_URL)
	@Mapper(PermissionMapper.class)
	public Permission getPermissionByHttpVerbAndUrl(@Bind(HTTP_VERB_FIELD) String httpVerb, @Bind(URL_FIELD) String url);

	@SqlQuery(GET_ALL_PERMISSIONS)
	@Mapper(PermissionMapper.class)
	public List<Permission> getAllPermissions();

	@SqlQuery(GET_ALL_PERMISSIONS_OF_A_GIVEN_ROLE)
	@Mapper(PermissionMapper.class)
	public List<Permission> getAllPermissionsOfAGivenRole(@Bind(ROLE_NAME_FIELD) String roleName);

	@SqlUpdate(DELETE_PERMISSION)
	public int deletePermission(@Bind(ID_FIELD) String permissionId);
}