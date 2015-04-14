package gp.e3.autheo.util;

import static org.junit.Assert.fail;

public class ExceptionUtilsForTests {

	public static void logAndFailOnUnexpectedException(Exception e) {

		e.printStackTrace();
		fail("Unexpected exception: " + e.getMessage());
	}
}