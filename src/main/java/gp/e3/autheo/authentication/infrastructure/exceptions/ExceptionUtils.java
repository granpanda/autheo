package gp.e3.autheo.authentication.infrastructure.exceptions;

import java.sql.SQLException;

public class ExceptionUtils {

	public static void throwIllegalStateException(SQLException e) {

		throw new IllegalStateException(e);
	}
}