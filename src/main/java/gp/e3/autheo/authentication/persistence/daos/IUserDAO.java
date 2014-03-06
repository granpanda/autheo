package gp.e3.autheo.authentication.persistence.daos;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.persistence.mappers.UserMapper;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface IUserDAO {
	
	public static final String NAME_FIELD = "name";
	public static final String USERNAME_FIELD = "username";
	public static final String PASSWORD_FIELD = "password";
	
	public static final String CREATE_USERS_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS users (name varchar(32) primary key, username varchar(32), password varchar(256));";
	
	public static final String INSERT_USER = "INSERT INTO users (name, username, password) VALUES (:name, :username:, :password);";
	public static final String GET_USER_BY_USERNAME = "SELECT * FROM users WHERE username = :username;";
	public static final String GET_ALL_USERS = "SELECT * FROM users;";
	public static final String UPDATE_USER_BY_USERNAME = "UPDATE users SET name = :name, password = :password WHERE username = :username;";
	public static final String DELETE_USER_BY_USERNAME = "DELETE FROM users WHERE username = :username;";
	
	
	@SqlUpdate(CREATE_USERS_TABLE_IF_NOT_EXISTS)
	public void createUsersTableIfNotExists();
	
	@SqlUpdate(INSERT_USER)
	public void createUser(@Bind(NAME_FIELD) String name, @Bind(USERNAME_FIELD) String username, 
						   @Bind(PASSWORD_FIELD) String password) throws Exception;
	
	@SqlQuery(GET_USER_BY_USERNAME)
	@Mapper(UserMapper.class)
	public User getUserByUsername(@Bind(NAME_FIELD) String username);
	
	@SqlQuery(GET_ALL_USERS)
	@Mapper(UserMapper.class)
	public List<User> getAllUsers();
	
	@SqlUpdate(UPDATE_USER_BY_USERNAME)
	public User updateUser(@Bind(USERNAME_FIELD) String username, @Bind(NAME_FIELD) String updatedName, 
						   @Bind(PASSWORD_FIELD) String updatedPassword);
	
	@SqlUpdate(DELETE_USER_BY_USERNAME)
	public User deleteUser(@Bind(USERNAME_FIELD) String username);
}