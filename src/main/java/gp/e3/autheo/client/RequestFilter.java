package gp.e3.autheo.client;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;

public class RequestFilter implements Filter {

	public static final String AUTHEO_URI = "http://localhost:9000/api/auth"; 
	public static final String TOKEN_HEADER = "Authorization";
	
	private String autheoUri;
	
	public RequestFilter(String autheoUri) {
		
		this.autheoUri = autheoUri;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	private HttpResponse getAuthorizationResponse(TicketDTO ticketDto) throws IOException, ClientProtocolException {
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpPut putRequest = new HttpPut(autheoUri);
		String appJson = ContentType.APPLICATION_JSON.toString();

		putRequest.addHeader("Accept", appJson);
		putRequest.addHeader("Content-Type", appJson);

		Gson gson = new Gson();
		StringEntity stringEntity = new StringEntity(gson.toJson(ticketDto), "UTF-8");
		putRequest.setEntity(stringEntity);
		
		return httpClient.execute(putRequest);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

		String tokenValue = httpRequest.getHeader(TOKEN_HEADER);
		String httpVerb = httpRequest.getMethod();
		String requestedUri = httpRequest.getRequestURI();

		TicketDTO ticketDto = new TicketDTO(tokenValue, httpVerb, requestedUri);

		HttpResponse authorizationResponse = getAuthorizationResponse(ticketDto);
		int authorizationStatusCode = authorizationResponse.getStatusLine().getStatusCode();
		
		if (authorizationStatusCode == 200) {

			// Go to the next filter. If this filter is the last one, then go to the resource.
			filterChain.doFilter(servletRequest, servletResponse);

		} else {
			
			// Response with the given status code.
			HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
			httpResponse.sendError(authorizationStatusCode);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}