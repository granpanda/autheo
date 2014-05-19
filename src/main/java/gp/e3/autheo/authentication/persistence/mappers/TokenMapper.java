package gp.e3.autheo.authentication.persistence.mappers;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenMapper {

	public static Token map(ResultSet resultSet) throws SQLException {
		
		Token retrievedToken = new Token(resultSet.getString(TokenDAO.TOKEN_VALUE_FIELD), resultSet.getString(TokenDAO.USERNAME_FIELD), 
				resultSet.getString(TokenDAO.ORGANIZATION_ID_FIELD), resultSet.getString(TokenDAO.ROLE_ID_FIELD), resultSet.getInt(TokenDAO.TOKEN_TYPE));
		
		return retrievedToken;
	}
}