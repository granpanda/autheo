package gp.e3.autheo.client.filter;

import gp.e3.autheo.client.dtos.TicketDTO;
import gp.e3.autheo.client.dtos.TokenDTO;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;

public class RequestFilter implements Filter {

	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_ATTRIBUTE = "Token";

	private String autheoUri;
	private Gson gson;

	public RequestFilter(String autheoUri) {

		gson = new Gson();
		this.autheoUri = autheoUri;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	private HttpResponse getAuthorizationResponse(TicketDTO ticketDto) throws IOException, ClientProtocolException {

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPut isAuthorizedRequest = new HttpPut(autheoUri);
		String appJson = ContentType.APPLICATION_JSON.toString();

		isAuthorizedRequest.addHeader("Accept", appJson);
		isAuthorizedRequest.addHeader("Content-Type", appJson);

		StringEntity stringEntity = new StringEntity(gson.toJson(ticketDto), "UTF-8");
		isAuthorizedRequest.setEntity(stringEntity);

		return httpClient.execute(isAuthorizedRequest);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

		String tokenValue = httpRequest.getHeader(TOKEN_HEADER);
		String httpVerb = httpRequest.getMethod();
		String requestedUri = httpRequest.getRequestURI();

		/* 
		 * Typically the browser sends an OPTIONS request by itself. Due to the browser does not 
		 * include the Authorization header we will allow all OPTIONS requests to pass.
		 */
		if (httpVerb.equalsIgnoreCase("OPTIONS")) {

			// Go to the next filter. If this filter is the last one, then go to the resource.
			filterChain.doFilter(servletRequest, servletResponse);

		} else {
			
			TicketDTO ticketDto = new TicketDTO(tokenValue, httpVerb, requestedUri);

			HttpResponse authorizationResponse = getAuthorizationResponse(ticketDto);
			int authorizationStatusCode = authorizationResponse.getStatusLine().getStatusCode();
			HttpEntity authorizationResponseEntity = authorizationResponse.getEntity();
			
			if (authorizationStatusCode == 200) {

				// Puts the token in the http request.
				InputStreamReader inputStreamReader = new InputStreamReader(authorizationResponseEntity.getContent());
				TokenDTO tokenDTO = gson.fromJson(inputStreamReader, TokenDTO.class);
				servletRequest.setAttribute(TOKEN_ATTRIBUTE, tokenDTO);
				inputStreamReader.close();

				// Go to the next filter. If this filter is the last one, then go to the resource.
				filterChain.doFilter(servletRequest, servletResponse);

			} else {

				// Response with the given status code.
				HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
				httpResponse.setContentType("application/json");
				httpResponse.setStatus(authorizationStatusCode);
			}
		}
	}

	public static TokenDTO getTokenFromHttpRequest(HttpServletRequest httpRequest) {

		TokenDTO tokenDTO = (TokenDTO) httpRequest.getAttribute(RequestFilter.TOKEN_ATTRIBUTE);
		return tokenDTO;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}