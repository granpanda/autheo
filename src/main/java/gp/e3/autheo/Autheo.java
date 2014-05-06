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

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner;
import com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jaxrs.listing.ResourceListingProvider;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.reader.ClassReaders;
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

	private JedisPool getRedisPoolInstance(AutheoConfig autheoConfig) {

		RedisConfig redisConfig = autheoConfig.getRedisConfig();
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), redisConfig.getHost(), redisConfig.getPort());

		return jedisPool;
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

	private RoleResource getRoleResource(final DBI jdbi, JedisPool jedisPool) {

		final IPermissionDAO permissionDao = jdbi.onDemand(IPermissionDAO.class);
		final PermissionBusiness permissionBusiness = new PermissionBusiness(permissionDao);

		final IRoleDAO roleDao = jdbi.onDemand(IRoleDAO.class);
		final RoleBusiness roleBusiness = new RoleBusiness(roleDao, permissionBusiness, jedisPool);

		return new RoleResource(roleBusiness);
	}

	private UserResource getUserResource(DBI jdbi, JedisPool jedisPool) 
			throws ClassNotFoundException {

		final IUserDAO userDAO = jdbi.onDemand(IUserDAO.class);
		final UserBusiness userBusiness = new UserBusiness(userDAO);

		final TokenDAO tokenDao = new TokenDAO(jedisPool);
		final TokenBusiness tokenBusiness = new TokenBusiness(tokenDao);

		return new UserResource(userBusiness, tokenBusiness);
	}

	private TicketResource getTicketResource(final DBI jdbi, JedisPool jedisPool) {

		TokenDAO tokenDao = new TokenDAO(jedisPool);
		TokenBusiness tokenBusiness = new TokenBusiness(tokenDao);

		IRoleDAO roleDao = jdbi.onDemand(IRoleDAO.class);
		IPermissionDAO permissionDao = jdbi.onDemand(IPermissionDAO.class);
		PermissionBusiness permissionBusiness = new PermissionBusiness(permissionDao);
		RoleBusiness roleBusiness = new RoleBusiness(roleDao, permissionBusiness, jedisPool);

		TicketBusiness ticketBusiness = new TicketBusiness(tokenBusiness, roleBusiness);

		return new TicketResource(ticketBusiness);
	}

	private void addSwaggerSupport(Environment environment) {

		environment.addResource(new ApiListingResourceJSON());
		environment.addProvider(new ApiDeclarationProvider());
		environment.addProvider(new ResourceListingProvider());

		ScannerFactory.setScanner(new DefaultJaxrsScanner());
		ClassReaders.setReader(new DefaultJaxrsApiReader());

		SwaggerConfig swaggerConfig = ConfigFactory.config();
		swaggerConfig.setApiVersion("1.0");
		swaggerConfig.setBasePath("/");
	}

	@Override
	public void run(AutheoConfig autheoConfig, Environment environment) throws Exception {

		// Jetty CORS support
		environment.addFilter(CrossOriginFilter.class, "/*")
		// See: http://download.eclipse.org/jetty/stable-9/xref/org/eclipse/jetty/servlets/CrossOriginFilter.html line 154.
		.setInitParam(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization")
		.setInitParam(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,HEAD,GET,POST,PUT,DELETE");

		// Access-Control-Request-Headers

		// Initialize JDBI
		final DBI jdbi = getJDBIInstance(autheoConfig, environment);

		// Initialize Redis
		JedisPool jedisPool = getRedisPoolInstance(autheoConfig);

		// Add Permission resource to the environment.
		PermissionResource permissionResource = getPermissionResource(jdbi);
		environment.addResource(permissionResource);

		// Add Role resource to the environment.
		RoleResource roleResource = getRoleResource(jdbi, jedisPool);
		environment.addResource(roleResource);

		// Add user resource to the environment.
		UserResource userResource = getUserResource(jdbi, jedisPool);
		environment.addResource(userResource);

		// Add ticket resource to the environment.
		TicketResource ticketResource = getTicketResource(jdbi, jedisPool);
		environment.addResource(ticketResource);

		// Swagger stuff.
		addSwaggerSupport(environment);
	}
}