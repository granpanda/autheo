package gp.e3.autheo.util;

import static org.junit.Assert.fail;

import java.sql.SQLException;

public class ExceptionUtilsForTests {

	public static void logUnexpectedException(SQLException e) {

		e.printStackTrace();
		fail("Unexpected exception: " + e.getMessage());
	}
}