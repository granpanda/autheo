package gp.e3.autheo.authentication.infrastructure.exceptions;

public class ExceptionUtils {

	public static void throwIllegalStateException(Exception e) {

		throw new IllegalStateException(e);
	}
}