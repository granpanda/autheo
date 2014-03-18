package gp.e3.autheo.authorization.persistence.mappers;

import static org.junit.Assert.*;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.persistence.daos.IPermissionDAO;
import gp.e3.autheo.util.PermissionFactoryForTests;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;

public class PermissionMapperTest {
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}
	
	@Test
	public void testMapMethod() {
		
		PermissionMapper permissionMapper = new PermissionMapper();
		
		Permission permission = PermissionFactoryForTests.getDefaultTestPermission();
		
		ResultSet mockedResultSet = Mockito.mock(ResultSet.class);
		
		try {
			
			int index = 0;
			
			Mockito.when(mockedResultSet.getInt(IPermissionDAO.ID_FIELD)).thenReturn(permission.getId());
			Mockito.when(mockedResultSet.getString(IPermissionDAO.NAME_FIELD)).thenReturn(permission.getName());
			Mockito.when(mockedResultSet.getString(IPermissionDAO.HTTP_VERB_FIELD)).thenReturn(permission.getHttpVerb());
			Mockito.when(mockedResultSet.getString(IPermissionDAO.URL_FIELD)).thenReturn(permission.getUrl());
			
			StatementContext mockedContext = Mockito.mock(StatementContext.class);
			
			Permission mappedPermission = permissionMapper.map(index, mockedResultSet, mockedContext);
			
			assertEquals(0, permission.compareTo(mappedPermission));
			
		} catch (SQLException e) {
			
			fail("The method should not throw an exception.");
		}
	}
}