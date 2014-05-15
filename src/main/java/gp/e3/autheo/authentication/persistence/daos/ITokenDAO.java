package gp.e3.autheo.authentication.persistence.daos;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.persistence.mappers.TokenMapper;

import java.util.List;

import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface ITokenDAO {
	
	public static final String TOKEN_VALUE_FIELD = "token_value";
	public static final String USERNAME_FIELD = "username";
	public static final String ORGANIZATION_ID_FIELD = "organization_id";
	public static final String ROLE_ID_FIELD = "role_id";
	
	public static final String CREATE_TOKENS_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS tokens (token_value varchar(128) primary key, username varchar(32), "
			+ "organization_id varchar(32), role_id varchar(32));";
	
	public static final String COUNT_TOKENS_TABLE = "SELECT COUNT(*) FROM tokens;";
	
	public static final String CREATE_TOKEN = "INSERT INTO tokens (token_value, username, organization_id, role_id) VALUES (:token_value, :username:, "
			+ ":organization_id, :role_id);";
	
	public static final String GET_ALL_TOKENS = "SELECT * FROM tokens;";
	public static final String UPDATE_TOKEN_BY_TOKEN_VALUE = "UPDATE tokens SET token_value = :token_value, username = :username, "
			+ "organization_id = :organization_id, role_id = :role_id WHERE token_value = :old_token_value;";
	
	public static final String DELETE_TOKEN_BY_TOKEN_VALUE = "DELETE FROM tokens WHERE token_value = :token_value;";
	public static final String DELETE_TOKEN_BY_USERNAME = "DELETE FROM tokens WHERE username = :username;";
	
	@SqlUpdate(CREATE_TOKENS_TABLE_IF_NOT_EXISTS)
	public void createTokensTableIfNotExists();

	@SqlQuery(COUNT_TOKENS_TABLE)
	public int countTokensTableRows();
	
	@SqlUpdate(CREATE_TOKEN)
	public void createToken(@Bind(TOKEN_VALUE_FIELD) String tokenValue, @Bind(USERNAME_FIELD) String username, @Bind(ORGANIZATION_ID_FIELD) String userOrganization, 
			@Bind(ROLE_ID_FIELD) String userRole) throws UnableToExecuteStatementException;
	
	@SqlQuery(GET_ALL_TOKENS)
	@Mapper(TokenMapper.class)
	public List<Token> getAllTokens();
	
	@SqlUpdate(UPDATE_TOKEN_BY_TOKEN_VALUE)
	public int updateTokenByTokenValue(@Bind("old_" + TOKEN_VALUE_FIELD) String oldTokenValue, @Bind(TOKEN_VALUE_FIELD) String updatedTokenValue, 
			@Bind(USERNAME_FIELD) String updatedUsername, @Bind(ORGANIZATION_ID_FIELD) String updatedUserOrganization, @Bind(ROLE_ID_FIELD) String updatedUserRole);
	
	@SqlUpdate(DELETE_TOKEN_BY_TOKEN_VALUE)
	public int deleteTokenByTokenValue(@Bind(TOKEN_VALUE_FIELD) String tokenValue);
	
	@SqlUpdate(DELETE_TOKEN_BY_USERNAME)
	public int deleteTokenByUsername(@Bind(USERNAME_FIELD) String username);
}