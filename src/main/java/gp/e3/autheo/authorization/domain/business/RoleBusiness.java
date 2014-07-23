package gp.e3.autheo.authorization.domain.business;

import gp.e3.autheo.authentication.infrastructure.utils.SqlUtils;
import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.domain.entities.Role;
import gp.e3.autheo.authorization.infrastructure.dtos.PermissionTuple;
import gp.e3.autheo.authorization.persistence.daos.RoleDAO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RoleBusiness {

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

	private void returnResource(Jedis jedis){
		redisPool.returnResource(jedis);
	}

	public Role createRole(Role role) {

		String roleName = role.getName();
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			roleDAO.createRole(dbConnection, roleName);
			List<Permission> rolePermissions = role.getPermissions();

			if (rolePermissions.size() > 0) {

				permissionBusiness.overwritePermissionsToRole(roleName, rolePermissions);
			}

		} catch (Exception e) {

			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}

		return role;
	}

	public Role getRoleByName(String roleName) {

		List<Permission> rolePermissions = permissionBusiness.getAllPermissionsOfAGivenRole(roleName);
		return new Role(roleName, rolePermissions);
	}

	public List<String> getAllRolesNames() {

		Connection dbConnection = null;
		List<String> rolesNames = new ArrayList<String>();
		
		try {
			
			dbConnection = dataSource.getConnection();
			rolesNames = roleDAO.getAllRolesNames(dbConnection);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return rolesNames;
	}

	public boolean updateRole(String roleName, List<Permission> updatedPermissions) {

		permissionBusiness.overwritePermissionsToRole(roleName, updatedPermissions);
		return addRolePermissionsToRedis(roleName); // Update role in Redis.
	}

	public boolean deleteRole(String roleName) {
		
		boolean roleWasDeleted = false;
		Connection dbConnection = null;
		Jedis redisClient = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			redisClient = getRedisClient();
			
			permissionBusiness.disassociateAllPermissionsFromRole(roleName);
			roleDAO.removeAllUsersFromRole(dbConnection, roleName);
			roleDAO.deleteRole(dbConnection, roleName);
			
			long keysRemoved = redisClient.del(roleName);
			roleWasDeleted = (keysRemoved > 0);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			returnResource(redisClient);
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return roleWasDeleted;
	}

	public boolean addUserToRole(String username, String roleName) {

		boolean userWasAddedToRole = false;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			int affectedRows = roleDAO.addUserToRole(dbConnection, username, roleName);
			userWasAddedToRole = (affectedRows == 1);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return userWasAddedToRole;
	}

	public boolean removeUserFromRole(String username, String roleName) {

		boolean userWasRemovedFromRole = false;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			int affectedRows = roleDAO.removeUserFromRole(dbConnection, username);
			userWasRemovedFromRole = (affectedRows > 0);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return userWasRemovedFromRole;
	}

	public List<Permission> getAllPermissionsOfAGivenRole(String roleName) {

		return permissionBusiness.getAllPermissionsOfAGivenRole(roleName);
	}

	public boolean rolePermissionsAreInRedis(String roleName) {

		Jedis redisClient = getRedisClient();
		String getResult = redisClient.get(roleName);
		returnResource(redisClient);

		System.out.println("areRolePermissionsInRedis()");
		System.out.println(getResult);

		return StringValidator.isValidString(getResult);
	}

	public boolean addRolePermissionsToRedis(String roleName) {

		Jedis redisClient = getRedisClient();

		boolean answer = false;

		List<Permission> rolePermissions = permissionBusiness.getAllPermissionsOfAGivenRole(roleName);

		if (rolePermissions != null && rolePermissions.size() > 0) {

			String permissionsString = "";

			for (Permission permission : rolePermissions) {

				permissionsString += permission.toString() + Permission.PERMISSION_SPLIT;
			}

			String setResult = redisClient.set(roleName, permissionsString);
			returnResource(redisClient);

			System.out.println("addRolePermissionsToRedis");
			System.out.println(setResult);

			answer = (setResult.equals("OK"));
		}

		// See: http://redis.io/commands/SET
		return answer;
	}

	public List<PermissionTuple> getRolePermissionsFromRedis(String roleName) {

		Jedis redisClient = getRedisClient();

		String rolePermissionsString = redisClient.get(roleName);
		returnResource(redisClient);

		List<PermissionTuple> rolePermissions = new ArrayList<PermissionTuple>();

		if (StringValidator.isValidString(rolePermissionsString)) {

			String[] permissionsArray = rolePermissionsString.split(Permission.PERMISSION_SPLIT);

			if (permissionsArray.length > 0) {

				for (int i = 0; i < permissionsArray.length; i++) {

					String permissionString = permissionsArray[i];
					String[] permissionAttributes = permissionString.split(Permission.ATTRIBUTE_SPLIT);
					rolePermissions.add(new PermissionTuple(permissionAttributes[2], permissionAttributes[3]));
				}
			}
		}

		return rolePermissions;
	}
}