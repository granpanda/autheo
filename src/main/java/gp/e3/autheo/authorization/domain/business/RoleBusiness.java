package gp.e3.autheo.authorization.domain.business;

import gp.e3.autheo.authentication.infrastructure.exceptions.ExceptionUtils;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.authorization.infrastructure.dtos.PermissionTuple;
import gp.e3.autheo.authorization.persistence.daos.RoleDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RoleBusiness {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleBusiness.class);

	private BasicDataSource dataSource;
	private JedisPool redisPool;
	private final RoleDAO roleDAO;
	private final PermissionBusiness permissionBusiness;
	
	public RoleBusiness(BasicDataSource dataSource, JedisPool redisPool, RoleDAO roleDAO, PermissionBusiness permissionBusiness) {
		
		this.dataSource = dataSource;
		this.redisPool = redisPool;
		this.roleDAO = roleDAO;
		this.permissionBusiness = permissionBusiness;
	}

	private Jedis getRedisClient(){
		return redisPool.getResource();
	}

	public Role createRole(Role role) {

		Role createdRole = null;
		String roleName = role.getName();
		List<Permission> rolePermissions = role.getPermissions();

		try (Connection dbConnection = dataSource.getConnection()) {

			
			int affectedRows = roleDAO.createRole(dbConnection, roleName);
			boolean roleWasCreated = (affectedRows == 1);
			
			if (roleWasCreated) {
				
				if (rolePermissions.size() > 0) {

					permissionBusiness.overwritePermissionsToRole(roleName, rolePermissions);
				}
				
				createdRole = role;
			}

		} catch (SQLException e) {

			LOGGER.error("createRole: check the role was not already created.", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return createdRole;
	}

	public Role getRoleByName(String roleName) {

		List<Permission> rolePermissions = permissionBusiness.getAllPermissionsOfAGivenRole(roleName);
		return new Role(roleName, rolePermissions);
	}

	public List<String> getAllRolesNames() {

		List<String> rolesNames = new ArrayList<String>();
		
		try (Connection dbConnection = dataSource.getConnection()) {
			
			rolesNames = roleDAO.getAllRolesNames(dbConnection);
			
		} catch (SQLException e) {
			
			LOGGER.error("getAllRolesNames", e);
			ExceptionUtils.throwIllegalStateException(e);
		}
		
		return rolesNames;
	}

	public boolean updateRole(String roleName, List<Permission> updatedPermissions) {

		permissionBusiness.overwritePermissionsToRole(roleName, updatedPermissions);
		return addRolePermissionsToRedis(roleName); // Update role in Redis.
	}

	public boolean deleteRole(String roleName) {
		
		boolean roleWasDeleted = false;
		Jedis redisClient = getRedisClient();
		
		try (Connection dbConnection = dataSource.getConnection()) {
			
			permissionBusiness.disassociateAllPermissionsFromRole(roleName);
			roleDAO.removeAllUsersFromRole(dbConnection, roleName);
			roleDAO.deleteRole(dbConnection, roleName);
			
			long keysRemoved = redisClient.del(roleName);
			roleWasDeleted = (keysRemoved > 0);
			
		} catch (JedisConnectionException e) {
			
			LOGGER.error("deleteRole", e);
			ExceptionUtils.throwIllegalStateException(e);
			
		} catch (SQLException e) {
			
			LOGGER.error("deleteRole", e);
			ExceptionUtils.throwIllegalStateException(e);
		
		} finally {
			
			redisClient.close();
		}
		
		return roleWasDeleted;
	}

	public boolean addUserToRole(String username, String roleName) {

		boolean userWasAddedToRole = false;
		
		try (Connection dbConnection = dataSource.getConnection()) {
			
			int affectedRows = roleDAO.addUserToRole(dbConnection, username, roleName);
			userWasAddedToRole = (affectedRows == 1);
			
		} catch (SQLException e) {
			
			LOGGER.error("addUserToRole", e);
			ExceptionUtils.throwIllegalStateException(e);
		}
		
		return userWasAddedToRole;
	}

	public boolean removeUserFromRole(String username, String roleName) {

		boolean userWasRemovedFromRole = false;
		
		try (Connection dbConnection = dataSource.getConnection()) {
			
			int affectedRows = roleDAO.removeUserFromRole(dbConnection, username);
			userWasRemovedFromRole = (affectedRows > 0);
			
		} catch (SQLException e) {
			
			LOGGER.error("removeUserFromRole", e);
			ExceptionUtils.throwIllegalStateException(e);
		}
		
		return userWasRemovedFromRole;
	}

	public List<Permission> getAllPermissionsOfAGivenRole(String roleName) {

		return permissionBusiness.getAllPermissionsOfAGivenRole(roleName);
	}

	public boolean rolePermissionsAreInRedis(String roleName) {

		Jedis redisClient = getRedisClient();
		String getResult = redisClient.get(roleName);
		redisClient.close();

		return StringValidator.isValidString(getResult);
	}

	public boolean addRolePermissionsToRedis(String roleName) {

		boolean answer = false;
		List<Permission> rolePermissions = permissionBusiness.getAllPermissionsOfAGivenRole(roleName);

		if (rolePermissions != null && rolePermissions.size() > 0) {

			Gson gson = new Gson();
			String rolePermissionsAsJson = gson.toJson(rolePermissions);
			
			Jedis redisClient = getRedisClient();
			String setResult = redisClient.set(roleName, rolePermissionsAsJson);
			redisClient.close();

			// See: http://redis.io/commands/SET
			answer = (setResult.equals("OK"));
		}
		
		return answer;
	}

	public List<PermissionTuple> getRolePermissionsFromRedis(String roleName) {

		List<PermissionTuple> rolePermissions = new ArrayList<PermissionTuple>();
		
		Jedis redisClient = getRedisClient();
		String rolePermissionsString = redisClient.get(roleName);
		redisClient.close();

		if (StringValidator.isValidString(rolePermissionsString)) {

			Gson gson = new Gson();
			List<Permission> listOfPermissions = gson.fromJson(rolePermissionsString, new TypeToken<List<Permission>>(){}.getType());

			if (listOfPermissions.size() > 0) {

				for (int i = 0; i < listOfPermissions.size(); i++) {

					Permission permission = listOfPermissions.get(i);
					rolePermissions.add(new PermissionTuple(permission.getHttpVerb(), permission.getUrl()));
				}
			}
		}

		return rolePermissions;
	}
}