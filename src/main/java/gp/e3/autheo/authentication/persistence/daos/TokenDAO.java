package gp.e3.autheo.authentication.persistence.daos;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.persistence.mappers.TokenMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TokenDAO {
	
	public static final String TOKEN_VALUE_FIELD = "token_value";
	public static final String USERNAME_FIELD = "username";
	public static final String ORGANIZATION_ID_FIELD = "organization_id";
	public static final String ROLE_ID_FIELD = "role_id";
	public static final String TOKEN_TYPE = "token_type";
	
	public static final String CREATE_TOKENS_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS tokens (token_value varchar(128), username varchar(32), "
			+ "organization_id varchar(32), role_id varchar(32), token_type TINYINT(10), PRIMARY KEY (organization_id, token_type));";
	
	public static final String COUNT_TOKENS_TABLE = "SELECT COUNT(*) FROM tokens;";
	
	public void createTokensTableIfNotExists(Connection dbConnection) throws SQLException {
		
		PreparedStatement prepareStatement = dbConnection.prepareStatement(CREATE_TOKENS_TABLE_IF_NOT_EXISTS);
		prepareStatement.executeUpdate();
		prepareStatement.close();
	}

	public int countTokensTableRows(Connection dbConnection) throws SQLException {
		
		PreparedStatement prepareStatement = dbConnection.prepareStatement(COUNT_TOKENS_TABLE);
		ResultSet resultSet = prepareStatement.executeQuery();
		
		int count = 0;
		
		if (resultSet.next()) {
			count = resultSet.getInt(1);
		}
		
		resultSet.close();
		prepareStatement.close();
		
		return count;
	}
	
	public int createToken(Connection dbConnection, Token token) throws SQLException {
		
		String createTokenSQL = "INSERT INTO tokens (token_value, username, organization_id, role_id, token_type) VALUES (?, ?, ?, ?, ?);";
		
		PreparedStatement prepareStatement = dbConnection.prepareStatement(createTokenSQL);
		
		prepareStatement.setString(1, token.getTokenValue());
		prepareStatement.setString(2, token.getUsername());
		prepareStatement.setString(3, token.getUserOrganization());
		prepareStatement.setString(4, token.getUserRole());
		prepareStatement.setInt(5, token.getTokenType());
		
		int rowsAffected = prepareStatement.executeUpdate();
		prepareStatement.close();
		
		return rowsAffected;
	}
	
	public Token getTokenByOrganizationId(Connection dbConnection, String organizationId) throws SQLException {
		
		String getTokenByOrganizationIdSQL = "SELECT * FROM tokens WHERE organization_id = ?;";
		
		PreparedStatement prepareStatement = dbConnection.prepareStatement(getTokenByOrganizationIdSQL);
		prepareStatement.setString(1, organizationId);
		
		ResultSet resultSet = prepareStatement.executeQuery();
		
		Token retrievedToken = null;
		
		 if (resultSet.next()) {
			 retrievedToken = TokenMapper.map(resultSet);
		 }
		 
		 resultSet.close();
		 prepareStatement.close();
		 
		 return retrievedToken;
	}
	
	public List<Token> getAllTokens(Connection dbConnection) throws SQLException {
		
		String getAllTokensSQL = "SELECT * FROM tokens;";
		
		PreparedStatement prepareStatement = dbConnection.prepareStatement(getAllTokensSQL);
		ResultSet resultSet = prepareStatement.executeQuery();
		
		List<Token> allTokens = new ArrayList<Token>();
		
		while (resultSet.next()) {
			allTokens.add(TokenMapper.map(resultSet));
		}
		
		resultSet.close();
		prepareStatement.close();
		
		return allTokens;
	}
	
	public int updateTokenByTokenValue(Connection dbConnection, String tokenValue, Token updatedToken) throws SQLException {
		
		String updateTokenByTokenValueSQL = "UPDATE tokens SET token_value = ?, username = ?, organization_id = ?, role_id = ?, token_type = ? WHERE token_value = ?;";
		PreparedStatement prepareStatement = dbConnection.prepareStatement(updateTokenByTokenValueSQL);
		prepareStatement.setString(1, updatedToken.getTokenValue());
		prepareStatement.setString(2, updatedToken.getUsername());
		prepareStatement.setString(3, updatedToken.getUserOrganization());
		prepareStatement.setString(4, updatedToken.getUserRole());
		prepareStatement.setInt(5, updatedToken.getTokenType());
		
		prepareStatement.setString(6, tokenValue);
		
		int rowsAffected = prepareStatement.executeUpdate();
		prepareStatement.close();
		
		return rowsAffected;
	}
	
	public int deleteTokenByTokenValue(Connection dbConnection, String tokenValue) throws SQLException {
		
		String deleteTokenByTokenValueSQL = "DELETE FROM tokens WHERE token_value = ?;";
		PreparedStatement prepareStatement = dbConnection.prepareStatement(deleteTokenByTokenValueSQL);
		prepareStatement.setString(1, tokenValue);
		
		int rowsAffected = prepareStatement.executeUpdate();
		prepareStatement.close();
		
		return rowsAffected;
	}
	
	public int deleteTokenByUsername(Connection dbConnection, String username) throws SQLException {
		
		String deleteTokenByUsernameSQL = "DELETE FROM tokens WHERE username = ?;";
		PreparedStatement prepareStatement = dbConnection.prepareStatement(deleteTokenByUsernameSQL);
		prepareStatement.setString(1, username);
		
		int rowsAffected = prepareStatement.executeUpdate();
		prepareStatement.close();
		
		return rowsAffected;
	}
}