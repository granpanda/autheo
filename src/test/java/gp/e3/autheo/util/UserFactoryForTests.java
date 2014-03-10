package gp.e3.autheo.util;

import gp.e3.autheo.authentication.domain.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserFactoryForTests {
	
	public static String getDefaultTestSalt() {
		
		return "123";
	}

	public static User getDefaultTestUser() {

		String name = "name";
		String username = "username";
		String password = "password";

		return new User(name, username, password);
	}

	public static User getDefaultTestUser(int userNumber) {

		String name = "name" + userNumber;
		String username = "username" + userNumber;
		String password = "password" + userNumber;

		return new User(name, username, password);
	}

	public static List<User> getUserList(int listSize) {

		List<User> userList = new ArrayList<User>();

		for (int i = 0; i < listSize; i++) {

			userList.add(getDefaultTestUser(i));
		}

		return userList;
	}
}