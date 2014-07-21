package gp.e3.autheo.authentication.persistence.mappers;

import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.persistence.daos.UserDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {

	private static User getUserFromResultSet(ResultSet resultSet) {

		User user = null;

		try {

			String name = resultSet.getString(UserDAO.NAME_FIELD);
			String username = resultSet.getString(UserDAO.USERNAME_FIELD);
			String password = resultSet.getString(UserDAO.PASSWORD_FIELD);
			boolean isApiClient = resultSet.getBoolean(UserDAO.IS_API_CLIENT_FIELD);
			String organizationId = resultSet.getString(UserDAO.ORGANIZATION_ID_FIELD);
			String roleId = resultSet.getString(UserDAO.ROLE_ID_FIELD);

			user = new User(name, username, password, isApiClient, organizationId, roleId);

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return user;
	}

	public static User getSingleUserFromResultSet(ResultSet resultSet) {

		User user = null;

		try {

			if (resultSet.next()) {
				user = getUserFromResultSet(resultSet);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return user;
	}

	public static List<User> getMultipleUsersFromResultSet(ResultSet resultSet) {

		List<User> usersList = new ArrayList<User>();

		try {

			while (resultSet.next()) {
				usersList.add(getUserFromResultSet(resultSet));
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return usersList;
	}
}