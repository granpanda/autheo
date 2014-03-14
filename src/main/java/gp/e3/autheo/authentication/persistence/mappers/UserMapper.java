package gp.e3.autheo.authentication.persistence.mappers;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.persistence.daos.IUserDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class UserMapper implements ResultSetMapper<User> {
	
	@Override
	public User map(int integer, ResultSet resultSet, StatementContext statementContext) throws SQLException {
		
		User user = new User(resultSet.getString(IUserDAO.NAME_FIELD), resultSet.getString(IUserDAO.USERNAME_FIELD), 
							 resultSet.getString(IUserDAO.PASSWORD_FIELD), 
							 resultSet.getString(IUserDAO.ORGANIZATION_ID_FIELD));
		
		return user;
	}
}