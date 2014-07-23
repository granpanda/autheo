package gp.e3.autheo.authentication.infrastructure.healthchecks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yammer.metrics.core.HealthCheck;

public class MySQLHealthCheck extends HealthCheck {
	
	private Connection dbConnection;
	
	public MySQLHealthCheck(String name, Connection dbConnection) {
		
		super(name);
		this.dbConnection = dbConnection;
	}

	@Override
	protected Result check() throws Exception {
		
		PreparedStatement prepareStatement = dbConnection.prepareStatement("SELECT 1;");
		ResultSet resultSet = prepareStatement.executeQuery();
		
		return resultSet.next()? Result.healthy() : Result.unhealthy("mysql failure.");
	}
}