package gp.e3.autheo.util;

import gp.e3.autheo.authentication.domain.entities.ApiUser;

import java.util.ArrayList;
import java.util.List;

public class ApiUserFactoryForTests {

	public static String getDefaultTestSalt() {

		return "123";
	}

	public static ApiUser getDefaultTestApiUser() {

		String name = "name";
		String username = "username";
		String password = "password";
		String organizationId = "organization";
		String roleId = "role";
		String tokenValue = "token";

		return new ApiUser(name, username, password, organizationId, roleId, tokenValue);
	}

	public static ApiUser getDefaultTestApiUser(int userNumber) {

		String name = "name" + userNumber;
		String username = "username" + userNumber;
		String password = "password" + userNumber;
		String organizationId = "organization" + userNumber;
		String roleId = "role" + userNumber;
		String tokenValue = "token" + userNumber;

		return new ApiUser(name, username, password, organizationId, roleId, tokenValue);
	}

	public static List<ApiUser> getUserList(int listSize) {

		List<ApiUser> apiUsersList = new ArrayList<ApiUser>();

		for (int i = 0; i < listSize; i++) {

			apiUsersList.add(getDefaultTestApiUser(i));
		}

		return apiUsersList;
	}
}