package gp.e3.autheo.authentication.service.resources.commons;

import javax.ws.rs.core.Response;

public class HttpCommonResponses {
	
	public static Response getInvalidSyntaxResponse() {

		String errorMessage = "The request cannot be fulfilled due to bad syntax.";
		return Response.status(400).entity(errorMessage).build();
	}
}