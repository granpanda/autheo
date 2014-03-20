package gp.e3.autheo;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.UserBusiness;
import gp.e3.autheo.authentication.infrastructure.RedisConfig;
import gp.e3.autheo.authentication.persistence.daos.IUserDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
import gp.e3.autheo.authentication.service.resources.UserResource;
import gp.e3.autheo.authorization.domain.business.PermissionBusiness;
import gp.e3.autheo.authorization.domain.business.RoleBusiness;
import gp.e3.autheo.authorization.domain.business.TicketBusiness;
import gp.e3.autheo.authorization.persistence.daos.IPermissionDAO;
import gp.e3.autheo.authorization.persistence.daos.IRoleDAO;
import gp.e3.autheo.authorization.service.PermissionResource;
import gp.e3.autheo.authorization.service.RoleResource;
import gp.e3.autheo.authorization.service.TicketResource;

import org.skife.jdbi.v2.DBI;

import redis.clients.jedis.Jedis;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.jdbi.bundles.DBIExceptionsBundle;

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

		bootstrap.addBundle(new DBIExceptionsBundle());
	}

	@Override
	public void run(AutheoConfig autheoConfig, Environment environment) throws Exception {

		// Initialize JDBI
		final DBI jdbi = getJDBIInstance(autheoConfig, environment);

		// Initialize Redis
		Jedis jedis = getRedisInstance(autheoConfig);

		// Add Permission resource to the environment.
		PermissionResource permissionResource = getPermissionResource(jdbi);
		environment.addResource(permissionResource);

		// Add Role resource to the environment.
		RoleResource roleResource = getRoleResource(jdbi, jedis);
		environment.addResource(roleResource);

		// Add user resource to the environment.
		UserResource userResource = getUserResource(jdbi, jedis);
		environment.addResource(userResource);

		// Add ticket resource to the environment.
		TicketResource ticketResource = getTicketResource(jdbi, jedis);
		environment.addResource(ticketResource);
	}

	private Jedis getRedisInstance(AutheoConfig autheoConfig) {

		RedisConfig redisConfig = autheoConfig.getRedisConfig();
		Jedis jedis = new Jedis(redisConfig.getHost(), redisConfig.getPort());

		return jedis;
	}

	private DBI getJDBIInstance(AutheoConfig autheoConfig, Environment environment) throws ClassNotFoundException {

		final DBIFactory dbiFactory = new DBIFactory();
		final DBI jdbi = dbiFactory.build(environment, 
				autheoConfig.getMySqlConfig(), "mysql");

		return jdbi;
	}

	private PermissionResource getPermissionResource(final DBI jdbi) {

		final IPermissionDAO permissionDao = jdbi.onDemand(IPermissionDAO.class);
		final PermissionBusiness permissionBusiness = new PermissionBusiness(permissionDao);
		PermissionResource permissionResource = new PermissionResource(permissionBusiness);

		return permissionResource;
	}

	private RoleResource getRoleResource(final DBI jdbi, final Jedis jedis) {

		final IPermissionDAO permissionDao = jdbi.onDemand(IPermissionDAO.class);
		final PermissionBusiness permissionBusiness = new PermissionBusiness(permissionDao);

		final IRoleDAO roleDao = jdbi.onDemand(IRoleDAO.class);
		final RoleBusiness roleBusiness = new RoleBusiness(roleDao, permissionBusiness, jedis);

		return new RoleResource(roleBusiness);
	}

	private UserResource getUserResource(DBI jdbi, Jedis jedis) 
			throws ClassNotFoundException {

		final IUserDAO userDAO = jdbi.onDemand(IUserDAO.class);
		final UserBusiness userBusiness = new UserBusiness(userDAO);

		final TokenDAO tokenDao = new TokenDAO(jedis);
		final TokenBusiness tokenBusiness = new TokenBusiness(tokenDao);

		return new UserResource(userBusiness, tokenBusiness);
	}

	private TicketResource getTicketResource(final DBI jdbi, Jedis jedis) {

		TokenDAO tokenDao = new TokenDAO(jedis);
		TokenBusiness tokenBusiness = new TokenBusiness(tokenDao);

		IRoleDAO roleDao = jdbi.onDemand(IRoleDAO.class);
		IPermissionDAO permissionDao = jdbi.onDemand(IPermissionDAO.class);
		PermissionBusiness permissionBusiness = new PermissionBusiness(permissionDao);
		RoleBusiness roleBusiness = new RoleBusiness(roleDao, permissionBusiness, jedis);

		TicketBusiness ticketBusiness = new TicketBusiness(tokenBusiness, roleBusiness);

		return new TicketResource(ticketBusiness);
	}
}