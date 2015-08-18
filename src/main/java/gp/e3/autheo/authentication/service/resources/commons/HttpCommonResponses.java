package gp.e3.autheo.authentication.service.resources.commons;

import gp.e3.autheo.authentication.service.resources.UserResource;

import javax.ws.rs.core.Response;
import java.util.Map;

public class HttpCommonResponses {
	
	public static Response getInvalidSyntaxResponse() {

		Map<String, String> errorMessage = UserResource.getStringInJsonFormat("error", "The request cannot be fulfilled due to bad syntax. Please check the URL and the payload of the HTTP request.");
		return Response.status(400).entity(errorMessage).build();
	}
}