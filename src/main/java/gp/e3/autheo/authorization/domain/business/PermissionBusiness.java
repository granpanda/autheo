package gp.e3.autheo.authorization.domain.business;

import gp.e3.autheo.authentication.infrastructure.utils.SqlUtils;
import gp.e3.autheo.authorization.domain.entities.Permission;
import gp.e3.autheo.authorization.persistence.daos.PermissionDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;

public class PermissionBusiness {

	private final BasicDataSource dataSource;
	private final PermissionDAO permissionDao;

	public PermissionBusiness(BasicDataSource dataSource, PermissionDAO permissionDao) {

		this.dataSource = dataSource;
		this.permissionDao = permissionDao;
	}

	public long createPermission(Permission permission) {

		long permissionId = 0;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			permissionId = permissionDao.createPermission(dbConnection, permission);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return permissionId;
	}

	public boolean overwritePermissionsToRole(String roleName, List<Permission> permissions) {

		boolean permissionsWereOverwritten = false;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();

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

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return permissionsWereOverwritten;
	}

	public boolean disassociatePermissionFromAllRoles(int permissionId) {

		boolean permissionWasDisassociated = false;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			int result = permissionDao.disassociatePermissionFromAllRoles(dbConnection, permissionId);
			permissionWasDisassociated = (result != 0);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return permissionWasDisassociated;
	}

	public boolean disassociateAllPermissionsFromRole(String roleName) {

		boolean permissionsWereDisassociatedFromRole = false;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			int result = permissionDao.disassociateAllPermissionsFromRole(dbConnection, roleName);
			permissionsWereDisassociatedFromRole = (result != 0);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return permissionsWereDisassociatedFromRole;
	}

	public Permission getPermissionById(long permissionId) {

		Permission permission = null;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			permission = permissionDao.getPermissionById(dbConnection, permissionId);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return permission;
	}

	public Permission getPermissionByHttpVerbAndUrl(String httpVerb, String url) {

		Permission permission = null;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			permission = permissionDao.getPermissionByHttpVerbAndUrl(dbConnection, httpVerb, url);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return permission;
	}

	public List<Permission> getAllPermissions() {

		List<Permission> permissions = new ArrayList<Permission>();
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			permissions = permissionDao.getAllPermissions(dbConnection);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return permissions;
	}

	public List<Permission> getAllPermissionsOfAGivenRole(String roleName) {

		List<Permission> allPermissions = new ArrayList<Permission>();

		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			allPermissions = permissionDao.getAllPermissionsOfAGivenRole(dbConnection, roleName);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return allPermissions;
	}

	public boolean deletePermission(long permissionId) {

		boolean permissionWasDeleted = false;
		Connection dbConnection = null;

		try {

			dbConnection = dataSource.getConnection();
			permissionDao.disassociatePermissionFromAllRoles(dbConnection, permissionId);
			int affectedRows = permissionDao.deletePermission(dbConnection, permissionId);

			permissionWasDeleted = (affectedRows == 1);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}

		return permissionWasDeleted;
	}
}