package gp.e3.autheo.authentication.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TokenMapperTest {
	
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testMapMethod() {

		String tokenValue = "tokenValue";
		String username = "username";
		String organizationId = "organizationId";
		String roleId = "roleId";
		int tokenType = 1;

		try {

			ResultSet resultSetMock = Mockito.mock(ResultSet.class);
			Mockito.when(resultSetMock.getString(TokenDAO.TOKEN_VALUE_FIELD)).thenReturn(tokenValue);
			Mockito.when(resultSetMock.getString(TokenDAO.USERNAME_FIELD)).thenReturn(username);
			Mockito.when(resultSetMock.getString(TokenDAO.ORGANIZATION_ID_FIELD)).thenReturn(organizationId);
			Mockito.when(resultSetMock.getString(TokenDAO.ROLE_ID_FIELD)).thenReturn(roleId);
			Mockito.when(resultSetMock.getInt(TokenDAO.TOKEN_TYPE)).thenReturn(tokenType);

			Token token = TokenMapper.map(resultSetMock);
			
			assertEquals(tokenValue, token.getTokenValue());
			assertEquals(username, token.getUsername());
			assertEquals(organizationId, token.getUserOrganization());
			assertEquals(roleId, token.getUserRole());
			assertEquals(tokenType, token.getTokenType());

		} catch (SQLException e) {
			
			fail(e.getMessage());
		}
	}
}