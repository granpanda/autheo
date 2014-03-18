package gp.e3.autheo.authorization.persistence.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.authorization.persistence.daos.IRoleDAO;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class RoleMapper implements ResultSetMapper<Role> {

	@Override
	public Role map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException {
		
		String roleName = resultSet.getString(IRoleDAO.NAME_FIELD);
		return new Role(roleName);
	}
}