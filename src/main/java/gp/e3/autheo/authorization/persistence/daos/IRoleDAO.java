package gp.e3.autheo.authorization.persistence.daos;

import gp.e3.autheo.authorization.domain.entities.Role;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface IRoleDAO {

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
	
	public static final String CREATE_ROLES_AND_USERS_TABLE_IF_NOT_EXISTS = 
			"CREATE TABLE IF NOT EXISTS roles_users (username VARCHAR(32) PRIMARY KEY, role_name VARCHAR(32));";
	
	public static final String CREATE_ROLE = "INSERT INTO roles (name) VALUES (:name);";
	
	public static final String GET_ROLE_BY_NAME = "SELECT * FROM roles WHERE name = :name;";
	
	public static final String GET_ALL_ROLES_NAMES = "SELECT name FROM roles;";
	
	public static final String DELETE_ROLE = "DELETE FROM roles WHERE name = :name;";
	
	public static final String ADD_USER_TO_ROLE = 
			"INSERT INTO roles_users (username, role_name) VALUES (:username, :role_name);";
	
	public static final String REMOVE_USER_FROM_ROLE = "DELETE FROM roles_users WHERE username = :username;";
	
	public static final String REMOVE_ALL_USERS_FROM_ROLE = "DELETE FROM roles_users WHERE role_name = :role_name;";

	//------------------------------------------------------------------------------------------------------
	// Database operations
	//------------------------------------------------------------------------------------------------------

	@SqlUpdate(CREATE_ROLES_TABLE_IF_NOT_EXISTS)
	
	public void createRolesTable();

	@SqlUpdate(CREATE_ROLE)
	public void createRole(@Bind(NAME_FIELD) String roleName);

	@SqlQuery(GET_ROLE_BY_NAME)
	public Role getRoleByName(@Bind(NAME_FIELD) String roleName);

	@SqlQuery(GET_ALL_ROLES_NAMES)
	public List<String> getAllRolesNames();

	@SqlUpdate(DELETE_ROLE)
	public int deleteRole(@Bind(NAME_FIELD) String roleName);

	@SqlUpdate(ADD_USER_TO_ROLE)
	public int addUserToRole(@Bind(USER_USERNAME_FIELD) String username, @Bind(ROLE_ROLE_NAME_FIELD) String roleName);

	@SqlUpdate(REMOVE_USER_FROM_ROLE)
	public int removeUserFromRole(@Bind(USER_USERNAME_FIELD) String username);
	
	@SqlUpdate(REMOVE_ALL_USERS_FROM_ROLE)
	public int removeAllUsersFromRole(@Bind(ROLE_ROLE_NAME_FIELD) String roleName);
}