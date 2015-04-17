package gp.e3.autheo.authorization.domain.business;

import gp.e3.autheo.authentication.infrastructure.exceptions.ExceptionUtils;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.persistence.daos.PermissionDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionBusiness {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionBusiness.class);

	private final BasicDataSource dataSource;
	private final PermissionDAO permissionDao;

	public PermissionBusiness(BasicDataSource dataSource, PermissionDAO permissionDao) {

		this.dataSource = dataSource;
		this.permissionDao = permissionDao;
	}

	public long createPermission(Permission permission) {

		long permissionId = 0;

		try (Connection dbConnection = dataSource.getConnection()) {

			permissionId = permissionDao.createPermission(dbConnection, permission);

		} catch (SQLException e) {

			LOGGER.error("createPermission: check the permission was not already created.", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return permissionId;
	}

	public boolean overwritePermissionsToRole(String roleName, List<Permission> permissions) {

		boolean permissionsWereOverwritten = false;

		try (Connection dbConnection = dataSource.getConnection()) {

			List<Long> permissionsIds = new ArrayList<Long>();

			for (Permission permission : permissions) {

				long permissionId = permission.getId();

				if (permissionId <= 0) {
					permissionId = createPermission(permission);
				}

				permissionsIds.add(permissionId);
			}

			permissionDao.disassociateAllPermissionsFromRole(dbConnection, roleName);
			int associatedPermissions = permissionDao.associateAllPermissionsToRole(dbConnection, roleName, permissionsIds);
			permissionsWereOverwritten = (associatedPermissions == permissions.size());

		} catch (SQLException e) {

			LOGGER.error("overwritePermissionsToRole", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return permissionsWereOverwritten;
	}

	public boolean disassociatePermissionFromAllRoles(int permissionId) {

		boolean permissionWasDisassociated = false;

		try (Connection dbConnection = dataSource.getConnection()) {

			int result = permissionDao.disassociatePermissionFromAllRoles(dbConnection, permissionId);
			permissionWasDisassociated = (result != 0);

		} catch (SQLException e) {

			LOGGER.error("disassociatePermissionFromAllRoles", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return permissionWasDisassociated;
	}

	public boolean disassociateAllPermissionsFromRole(String roleName) {

		boolean permissionsWereDisassociatedFromRole = false;

		try (Connection dbConnection = dataSource.getConnection()) {

			int result = permissionDao.disassociateAllPermissionsFromRole(dbConnection, roleName);
			permissionsWereDisassociatedFromRole = (result != 0);

		} catch (SQLException e) {

			LOGGER.error("disassociateAllPermissionsFromRole", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return permissionsWereDisassociatedFromRole;
	}

	public Permission getPermissionById(long permissionId) {

		Permission permission = null;

		try (Connection dbConnection = dataSource.getConnection()) {

			permission = permissionDao.getPermissionById(dbConnection, permissionId);

		} catch (SQLException e) {

			LOGGER.error("getPermissionById", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return permission;
	}

	public Permission getPermissionByHttpVerbAndUrl(String httpVerb, String url) {

		Permission permission = null;

		try (Connection dbConnection = dataSource.getConnection()) {

			permission = permissionDao.getPermissionByHttpVerbAndUrl(dbConnection, httpVerb, url);

		} catch (SQLException e) {

			LOGGER.error("getPermissionByHttpVerbAndUrl", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return permission;
	}

	public List<Permission> getAllPermissions() {

		List<Permission> permissions = new ArrayList<Permission>();

		try (Connection dbConnection = dataSource.getConnection()) {

			permissions = permissionDao.getAllPermissions(dbConnection);

		} catch (SQLException e) {

			LOGGER.error("getAllPermissions", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return permissions;
	}

	public List<Permission> getAllPermissionsOfAGivenRole(String roleName) {

		List<Permission> allPermissions = new ArrayList<Permission>();

		try (Connection dbConnection = dataSource.getConnection()) {

			allPermissions = permissionDao.getAllPermissionsOfAGivenRole(dbConnection, roleName);

		} catch (SQLException e) {

			LOGGER.error("getAllPermissionsOfAGivenRole", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return allPermissions;
	}

	public boolean deletePermission(long permissionId) {

		boolean permissionWasDeleted = false;

		try (Connection dbConnection = dataSource.getConnection()) {

			permissionDao.disassociatePermissionFromAllRoles(dbConnection, permissionId);
			int affectedRows = permissionDao.deletePermission(dbConnection, permissionId);

			permissionWasDeleted = (affectedRows == 1);

		} catch (SQLException e) {

			LOGGER.error("deletePermission", e);
			ExceptionUtils.throwIllegalStateException(e);
		}

		return permissionWasDeleted;
	}
}