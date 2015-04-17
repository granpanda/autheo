package gp.e3.autheo.authentication.service.resources;

import gp.e3.autheo.authentication.domain.business.TokenBusiness;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.service.resources.commons.HttpCommonResponses;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

@Path("/organizations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrganizationResource {

	private final TokenBusiness tokenBusiness;

	public OrganizationResource(TokenBusiness tokenBusiness) {

		this.tokenBusiness = tokenBusiness;
	}

	private String getStringInJsonFormat(String message) {

		Gson gson = new Gson();
		return gson.toJson(message);
	}

	@GET
	@Path("/{organizationId}/module-token")
	public Response getModuleTokenByUserOrganization(@PathParam("organizationId") String organizationId) {

		Response response = null;

		if (!StringUtils.isBlank(organizationId)) {

			Token moduleToken = tokenBusiness.getModuleToken(organizationId);

			if (moduleToken != null) {

				response = Response.status(200).entity(moduleToken).build();

			} else {

				String errorMessage = getStringInJsonFormat("The organization " + organizationId + "does not have a module token.");
				response = Response.status(404).entity(errorMessage).build();
			}

		} else {

			response = HttpCommonResponses.getInvalidSyntaxResponse();
		}

		return response;
	}
}