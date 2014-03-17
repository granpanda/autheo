package gp.e3.autheo.authorization.persistence.mappers;

import static org.junit.Assert.*;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.authorization.persistence.daos.IRoleDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;

public class RoleMapperTest {
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}
	
	@Test
	public void testMapMethod() {
		
		RoleMapper roleMapper = new RoleMapper();
		
		String roleName = "roleName";
		
		ResultSet mockedResultSet = Mockito.mock(ResultSet.class);
		
		try {
			
			int index = 0;
			Mockito.when(mockedResultSet.getString(IRoleDAO.NAME_FIELD)).thenReturn(roleName);
			StatementContext mockedContext = Mockito.mock(StatementContext.class);
			
			Role mappedRole = roleMapper.map(index, mockedResultSet, mockedContext);
			
			assertEquals(roleName, mappedRole.getName());
			
		} catch (SQLException e) {
			
			fail("The method should not throw an exception.");
		}
	}
}