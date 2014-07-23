package gp.e3.autheo;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.UserBusiness;
import gp.e3.autheo.authentication.infrastructure.config.MySQLConfig;
import gp.e3.autheo.authentication.infrastructure.config.RedisConfig;
import gp.e3.autheo.authentication.infrastructure.healthchecks.RedisHealthCheck;
import gp.e3.autheo.authentication.infrastructure.utils.SqlUtils;
import gp.e3.autheo.authentication.persistence.daos.TokenCacheDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
import gp.e3.autheo.authentication.persistence.daos.UserDAO;
import gp.e3.autheo.authentication.service.resources.TokenResource;
import gp.e3.autheo.authentication.service.resources.UserResource;
import gp.e3.autheo.authorization.domain.business.PermissionBusiness;
import gp.e3.autheo.authorization.domain.business.RoleBusiness;
import gp.e3.autheo.authorization.domain.business.TicketBusiness;
import gp.e3.autheo.authorization.persistence.daos.PermissionDAO;
import gp.e3.autheo.authorization.persistence.daos.RoleDAO;
import gp.e3.autheo.authorization.service.PermissionResource;
import gp.e3.autheo.authorization.service.RoleResource;
import gp.e3.autheo.authorization.service.TicketResource;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * Autheo main class
 */
public class Autheo extends Service<AutheoConfig> {

	public static void main( String[] args ) throws Exception {

		Autheo autheo = new Autheo();
		autheo.run(args);
	}

	@Override
	public void initialize(Bootstrap<AutheoConfig> bootstrap) {
		
	}

	private JedisPool getRedisPoolInstance(RedisConfig redisConfig) {

		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), redisConfig.getHost(), redisConfig.getPort(), 
				Protocol.DEFAULT_TIMEOUT, null, redisConfig.getDatabase());

		return jedisPool;
	}

	private PermissionResource getPermissionResource(BasicDataSource dataSource) {

		final PermissionDAO permissionDao = new PermissionDAO();
		final PermissionBusiness permissionBusiness = new PermissionBusiness(dataSource, permissionDao);
		PermissionResource permissionResource = new PermissionResource(permissionBusiness);

		return permissionResource;
	}

	private RoleResource getRoleResource(BasicDataSource dataSource, JedisPool jedisPool) {

		final PermissionDAO permissionDao = new PermissionDAO();
		final PermissionBusiness permissionBusiness = new PermissionBusiness(dataSource, permissionDao);

		final RoleDAO roleDao = new RoleDAO();
		final RoleBusiness roleBusiness = new RoleBusiness(dataSource, jedisPool, roleDao, permissionBusiness);

		return new RoleResource(roleBusiness);
	}

	private BasicDataSource getInitializedDataSource(MySQLConfig mySqlConfig) {

		BasicDataSource basicDataSource = new BasicDataSource();

		basicDataSource.setDriverClassName(mySqlConfig.getDriverClass());
		basicDataSource.setUrl(mySqlConfig.getUrl());
		basicDataSource.setUsername(mySqlConfig.getUsername());
		basicDataSource.setPassword(mySqlConfig.getPassword());

		// int maxValue = 100;
		// basicDataSource.setMaxIdle(maxValue);
		// basicDataSource.setMaxTotal(maxValue);

		return basicDataSource;
	}

	private void initializeTablesIfNeeded(BasicDataSource dataSource) {

		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			
			TokenDAO tokenDAO = new TokenDAO();
			tokenDAO.createTokensTableIfNotExists(dbConnection);

			UserDAO userDAO = new UserDAO();
			userDAO.createUsersTableIfNotExists(dbConnection);

			PermissionDAO permissionDAO = new PermissionDAO();
			permissionDAO.createPermissionsTable(dbConnection);
			permissionDAO.createPermissionsUniqueIndex(dbConnection);
			
			RoleDAO roleDAO = new RoleDAO();
			roleDAO.createRolesTable(dbConnection);
			roleDAO.createRolesAndPermissionsTable(dbConnection);
			roleDAO.createRolesAndUsersTable(dbConnection);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
	}

	private TokenResource getTokenResource(BasicDataSource dataSource, JedisPool jedisPool) {

		TokenDAO tokenDAO = new TokenDAO();
		TokenCacheDAO tokenCacheDao = new TokenCacheDAO(jedisPool);
		TokenBusiness tokenBusiness = new TokenBusiness(dataSource, tokenDAO, tokenCacheDao);

		return new TokenResource(tokenBusiness);
	}

	private UserResource getUserResource(BasicDataSource dataSource, JedisPool jedisPool) 
			throws ClassNotFoundException {

		final UserDAO userDAO = new UserDAO();
		final UserBusiness userBusiness = new UserBusiness(dataSource, userDAO);

		TokenDAO tokenDAO = new TokenDAO();
		TokenCacheDAO tokenCacheDao = new TokenCacheDAO(jedisPool);
		TokenBusiness tokenBusiness = new TokenBusiness(dataSource, tokenDAO, tokenCacheDao);

		return new UserResource(userBusiness, tokenBusiness);
	}

	private TicketResource getTicketResource(BasicDataSource dataSource, JedisPool jedisPool) {

		TokenDAO tokenDAO = new TokenDAO();
		TokenCacheDAO tokenCacheDao = new TokenCacheDAO(jedisPool);
		TokenBusiness tokenBusiness = new TokenBusiness(dataSource, tokenDAO, tokenCacheDao);

		RoleDAO roleDao = new RoleDAO();
		PermissionDAO permissionDao = new PermissionDAO();
		PermissionBusiness permissionBusiness = new PermissionBusiness(dataSource, permissionDao);
		RoleBusiness roleBusiness = new RoleBusiness(dataSource, jedisPool, roleDao, permissionBusiness);

		TicketBusiness ticketBusiness = new TicketBusiness(tokenBusiness, roleBusiness);

		return new TicketResource(ticketBusiness);
	}

	private void addRedisHealthCheck(Environment environment, Jedis redisClient) {

		String healthCheckName = "redis";
		environment.addHealthCheck(new RedisHealthCheck(healthCheckName, redisClient));
	}

	@Override
	public void run(AutheoConfig autheoConfig, Environment environment) throws Exception {

		// Jetty CORS support
		environment.addFilter(CrossOriginFilter.class, "/*")
		// See: http://download.eclipse.org/jetty/stable-9/xref/org/eclipse/jetty/servlets/CrossOriginFilter.html line 154.
		.setInitParam(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization")
		.setInitParam(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,HEAD,GET,POST,PUT,DELETE,PATCH"); // Access-Control-Request-Headers

		// Get Configurations.
		MySQLConfig mySqlConfig = autheoConfig.getMySqlConfig();
		RedisConfig redisConfig = autheoConfig.getRedisConfig();

		// Initialize Redis
		JedisPool jedisPool = getRedisPoolInstance(redisConfig);

		// Add health checks.
		addRedisHealthCheck(environment, jedisPool.getResource());

		// Initialize data source.
		BasicDataSource dataSource = getInitializedDataSource(mySqlConfig);
		initializeTablesIfNeeded(dataSource);

		// Add Permission resource to the environment.
		PermissionResource permissionResource = getPermissionResource(dataSource);
		environment.addResource(permissionResource);

		// Add Role resource to the environment.
		RoleResource roleResource = getRoleResource(dataSource, jedisPool);
		environment.addResource(roleResource);

		// Add token resource to the environment.
		TokenResource tokenResource = getTokenResource(dataSource, jedisPool);
		environment.addResource(tokenResource);

		// Add user resource to the environment.
		UserResource userResource = getUserResource(dataSource, jedisPool);
		environment.addResource(userResource);

		// Add ticket resource to the environment.
		TicketResource ticketResource = getTicketResource(dataSource, jedisPool);
		environment.addResource(ticketResource);
	}
}