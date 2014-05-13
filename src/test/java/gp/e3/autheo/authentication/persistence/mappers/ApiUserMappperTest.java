package gp.e3.autheo.authentication.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.entities.ApiUser;
import gp.e3.autheo.authentication.persistence.daos.IApiUserDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;

public class ApiUserMappperTest {
	
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testMapMethod() {

		ApiUserMapper apiUserMapper = new ApiUserMapper();

		String name = "name";
		String username = "username";
		String password = "password";
		String organizationId = "organizationId";
		String roleId = "roleId";
		String tokenValue = "tokenValue";

		int intValue = 0;

		try {

			ResultSet resultSetMock = Mockito.mock(ResultSet.class);
			Mockito.when(resultSetMock.getString(IApiUserDAO.NAME_FIELD)).thenReturn(name);
			Mockito.when(resultSetMock.getString(IApiUserDAO.USERNAME_FIELD)).thenReturn(username);
			Mockito.when(resultSetMock.getString(IApiUserDAO.PASSWORD_FIELD)).thenReturn(password);
			Mockito.when(resultSetMock.getString(IApiUserDAO.ORGANIZATION_ID_FIELD)).thenReturn(organizationId);
			Mockito.when(resultSetMock.getString(IApiUserDAO.ROLE_ID_FIELD)).thenReturn(roleId);
			Mockito.when(resultSetMock.getString(IApiUserDAO.TOKEN_VALUE_FIELD)).thenReturn(tokenValue);

			StatementContext statementContextMock = Mockito.mock(StatementContext.class);

			ApiUser apiUser = apiUserMapper.map(intValue, resultSetMock, statementContextMock);
			
			assertEquals(name, apiUser.getName());
			assertEquals(username, apiUser.getUsername());
			assertEquals(password, apiUser.getPassword());
			assertEquals(organizationId, apiUser.getOrganizationId());
			assertEquals(roleId, apiUser.getRoleId());
			assertEquals(tokenValue, apiUser.getTokenValue());

		} catch (SQLException e) {
			
			fail(e.getMessage());
		}
	}
}