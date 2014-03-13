package gp.e3.autheo;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.business.UserBusiness;
import gp.e3.autheo.authentication.infrastructure.RedisConfig;
import gp.e3.autheo.authentication.persistence.daos.IUserDAO;
import gp.e3.autheo.authentication.persistence.daos.TokenDAO;
import gp.e3.autheo.authentication.service.resources.UserResource;

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
		
		// Add user resource to the environment.
		UserResource userResource = getUserResource(autheoConfig, environment);
		environment.addResource(userResource);
	}

	private UserResource getUserResource(AutheoConfig autheoConfig, Environment environment) 
			throws ClassNotFoundException {
		
		// MySQL
		final DBIFactory dbiFactory = new DBIFactory();
		final DBI jdbi = dbiFactory.build(environment, 
				autheoConfig.getAuthenticationConfig().getAuthenticationDatabase(), "mysql");
		
		final IUserDAO userDAO = jdbi.onDemand(IUserDAO.class);
		UserBusiness userBusiness = new UserBusiness(userDAO);
		
		// Redis
		RedisConfig redisConfig = autheoConfig.getRedisConfig();
		Jedis jedis = new Jedis(redisConfig.getHost(), redisConfig.getPort()); // Init Jedis from RedisConfig.
		final TokenDAO tokenDao = new TokenDAO(jedis);
		TokenBusiness tokenBusiness = new TokenBusiness(tokenDao);
		
		UserResource userResource = new UserResource(userBusiness, tokenBusiness);
		
		return userResource;
	}
}