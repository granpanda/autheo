package gp.e3.autheo;

import gp.e3.autheo.authentication.domain.business.UserBusiness;
import gp.e3.autheo.authentication.persistence.daos.IUserDAO;
import gp.e3.autheo.authentication.service.resources.UserResource;

import org.skife.jdbi.v2.DBI;

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
		
		final DBIFactory dbiFactory = new DBIFactory();
		final DBI jdbi = dbiFactory.build(environment, 
				autheoConfig.getAuthenticationConfig().getAuthenticationDatabase(), "mysql");
		
		// Add user resource to the environment.
		UserResource userResource = getUserResource(jdbi);
		environment.addResource(userResource);
	}

	private UserResource getUserResource(final DBI jdbi) {
		
		final IUserDAO userDAO = jdbi.onDemand(IUserDAO.class);
		UserBusiness userBusiness = new UserBusiness(userDAO);
		UserResource userResource = new UserResource(userBusiness);
		
		return userResource;
	}
}