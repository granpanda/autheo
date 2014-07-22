package gp.e3.autheo.authorization.persistence.mappers;

import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.persistence.daos.PermissionDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PermissionMapper {
	
	private static Permission getPermissionFromResultSet(ResultSet resultSet) {
		
		Permission permission = null;
		
		try {
			
			int id = resultSet.getInt(PermissionDAO.ID_FIELD);
			String name = resultSet.getString(PermissionDAO.NAME_FIELD);
			String httpVerb = resultSet.getString(PermissionDAO.HTTP_VERB_FIELD);
			String url = resultSet.getString(PermissionDAO.URL_FIELD);
			
			permission = new Permission(id, name, httpVerb, url);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return permission;
	}
	
	public static Permission getSinglePermission(ResultSet resultSet) {
		
		Permission permission = null;
		
		try {
			
			if (resultSet.next()) {
				permission = getPermissionFromResultSet(resultSet);
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return permission;
	}
	
	public static List<Permission> getMultiplePermissions(ResultSet resultSet) {
		
		List<Permission> permissions = new ArrayList<Permission>();
		
		try {
			
			while (resultSet.next()) {
				permissions.add(getPermissionFromResultSet(resultSet));
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return permissions;
	}
}