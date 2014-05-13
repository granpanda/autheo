package gp.e3.autheo.authentication.persistence.daos;

import gp.e3.autheo.authentication.domain.entities.ApiUser;
import gp.e3.autheo.authentication.persistence.mappers.ApiUserMapper;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface IApiUserDAO {
	
	public static final String NAME_FIELD = "name";
	public static final String USERNAME_FIELD = "username";
	public static final String PASSWORD_FIELD = "password";
	public static final String SALT_FIELD = "salt";
	public static final String ORGANIZATION_ID_FIELD = "organization_id";
	public static final String ROLE_ID_FIELD = "role_id";
	public static final String TOKEN_VALUE_FIELD = "token_value";
	
	public static final String CREATE_API_USERS_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS api_users (name varchar(32), username varchar(32) primary key, "
			+ "password varchar(256), salt varchar(256), organization_id varchar(32), role_id varchar(32), token_value varchar(128));";
	
	public static final String COUNT_API_USERS_TABLE = "SELECT COUNT(*) FROM api_users;";
	
	public static final String INSERT_API_USER = "INSERT INTO api_users (name, username, password, salt, organization_id, role_id, token_value) VALUES (:name, :username:, "
			+ ":password, :salt, :organization_id, :role_id, :token_value);";
	
	public static final String GET_API_USER_BY_USERNAME = "SELECT * FROM api_users WHERE username = :username;";
	public static final String GET_API_USER_BY_TOKEN = "SELECT * FROM api_users WHERE token_value = :token_value;";
	
	public static final String GET_PASSWORD_BY_USERNAME = "SELECT password FROM api_users WHERE username = :username;";
	public static final String GET_ALL_API_USERS = "SELECT * FROM api_users;";
	
	public static final String UPDATE_API_USER_BY_USERNAME = "UPDATE api_users SET name = :name, password = :password, role_id = :role_id, token_value = :token_value "
			+ "WHERE username = :username;";
	
	public static final String DELETE_API_USER_BY_USERNAME = "DELETE FROM api_users WHERE username = :username;";
	
	
	@SqlUpdate(CREATE_API_USERS_TABLE_IF_NOT_EXISTS)
	public void createApiUsersTableIfNotExists();
	
	@SqlQuery(COUNT_API_USERS_TABLE)
	public int countApiUsersTable();
	
	@SqlUpdate(INSERT_API_USER)
	public void createApiUser(@Bind(NAME_FIELD) String name, 
						   @Bind(USERNAME_FIELD) String username, 
						   @Bind(PASSWORD_FIELD) String password, 
						   @Bind(SALT_FIELD) String salt,
						   @Bind(ORGANIZATION_ID_FIELD) String organizationId,
						   @Bind(ROLE_ID_FIELD) String roleId,
						   @Bind(TOKEN_VALUE_FIELD) String tokenValue) throws Exception;
	
	@SqlQuery(GET_API_USER_BY_USERNAME)
	@Mapper(ApiUserMapper.class)
	public ApiUser getApiUserByUsername(@Bind(USERNAME_FIELD) String username);
	
	@SqlQuery(GET_API_USER_BY_TOKEN)
	@Mapper(ApiUserMapper.class)
	public ApiUser getApiUserByToken(@Bind(TOKEN_VALUE_FIELD) String tokenValue);
	
	@SqlQuery(GET_ALL_API_USERS)
	@Mapper(ApiUserMapper.class)
	public List<ApiUser> getAllApiUsers();
	
	@SqlQuery(GET_PASSWORD_BY_USERNAME)
	public String getPasswordByUsername(@Bind(USERNAME_FIELD) String username);
	
	@SqlUpdate(UPDATE_API_USER_BY_USERNAME)
	public int updateApiUser(@Bind(USERNAME_FIELD) String username, @Bind(NAME_FIELD) String updatedName, @Bind(PASSWORD_FIELD) String updatedPassword, 
			@Bind(SALT_FIELD) String updatedSalt, @Bind(ROLE_ID_FIELD) String updatedRoleId, @Bind(TOKEN_VALUE_FIELD) String tokenValue);
	
	@SqlUpdate(DELETE_API_USER_BY_USERNAME)
	public int deleteApiUser(@Bind(USERNAME_FIELD) String username);
}