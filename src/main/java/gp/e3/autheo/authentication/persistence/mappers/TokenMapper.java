package gp.e3.autheo.authentication.persistence.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.persistence.daos.ITokenDAO;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class TokenMapper implements ResultSetMapper<Token> {

	@Override
	public Token map(int index, ResultSet resultSet, StatementContext context) throws SQLException {
		
		Token token = new Token(resultSet.getString(ITokenDAO.TOKEN_VALUE_FIELD), resultSet.getString(ITokenDAO.USERNAME_FIELD), 
				resultSet.getString(ITokenDAO.ORGANIZATION_ID_FIELD), resultSet.getString(ITokenDAO.ROLE_ID_FIELD));
		
		return token;
	}
}