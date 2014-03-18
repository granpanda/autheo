package gp.e3.autheo.authorization.persistence.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.persistence.daos.IPermissionDAO;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class PermissionMapper implements ResultSetMapper<Permission> {

	@Override
	public Permission map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException {
		
		int id = resultSet.getInt(IPermissionDAO.ID_FIELD);
		String name = resultSet.getString(IPermissionDAO.NAME_FIELD);
		String httpVerb = resultSet.getString(IPermissionDAO.HTTP_VERB_FIELD);
		String url = resultSet.getString(IPermissionDAO.URL_FIELD);
		
		return new Permission(id, name, httpVerb, url);
	}
}