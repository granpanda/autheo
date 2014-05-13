package gp.e3.autheo.authentication.persistence.mappers;

import gp.e3.autheo.authentication.domain.entities.ApiUser;
import gp.e3.autheo.authentication.persistence.daos.IApiUserDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class ApiUserMapper implements ResultSetMapper<ApiUser> {
	
	@Override
	public ApiUser map(int integer, ResultSet resultSet, StatementContext statementContext) throws SQLException {
		
		ApiUser apiUser = new ApiUser(resultSet.getString(IApiUserDAO.NAME_FIELD), resultSet.getString(IApiUserDAO.USERNAME_FIELD), 
				resultSet.getString(IApiUserDAO.PASSWORD_FIELD), resultSet.getString(IApiUserDAO.ORGANIZATION_ID_FIELD),
				resultSet.getString(IApiUserDAO.ROLE_ID_FIELD), resultSet.getString(IApiUserDAO.TOKEN_VALUE_FIELD));
		
		return apiUser;
	}
}