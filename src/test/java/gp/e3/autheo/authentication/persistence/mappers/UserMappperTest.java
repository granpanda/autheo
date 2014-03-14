package gp.e3.autheo.authentication.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.persistence.daos.IUserDAO;
import gp.e3.autheo.authentication.persistence.mappers.UserMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;

public class UserMappperTest {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testMapMethod() {

		UserMapper userMapper = new UserMapper();

		String name = "name";
		String username = "username";
		String password = "password";

		int intValue = 0;

		try {

			ResultSet resultSetMock = Mockito.mock(ResultSet.class);
			Mockito.when(resultSetMock.getString(IUserDAO.NAME_FIELD)).thenReturn(name);
			Mockito.when(resultSetMock.getString(IUserDAO.USERNAME_FIELD)).thenReturn(username);
			Mockito.when(resultSetMock.getString(IUserDAO.PASSWORD_FIELD)).thenReturn(password);

			StatementContext statementContextMock = Mockito.mock(StatementContext.class);

			User user = userMapper.map(intValue, resultSetMock, statementContextMock);
			
			assertEquals(name, user.getName());
			assertEquals(username, user.getUsername());
			assertEquals(password, user.getPassword());

		} catch (SQLException e) {
			
			fail(e.getMessage());
		}
	}
}