package gp.e3.autheo.authorization.infrastructure.dtos;

import gp.e3.autheo.authorization.infrastructure.validators.E3UrlValidator;
import gp.e3.autheo.authorization.infrastructure.validators.HttpVerbValidator;

public class PermissionTuple {
	
	private final String httpVerb;
	private final String url;
	
	public PermissionTuple(String httpVerb, String url) {
		
		this.httpVerb = httpVerb;
		this.url = url;
	}

	public String getHttpVerb() {
		return httpVerb;
	}

	public String getUrl() {
		return url;
	}
	
	public boolean areTheSamePermission(PermissionTuple tuplePermission) {
		
		boolean httpVerbsAreTheSame = HttpVerbValidator.isValidHttpVerb(httpVerb) && 
									  httpVerb.equalsIgnoreCase(tuplePermission.getHttpVerb());
		
		boolean urlsMatch = E3UrlValidator.urlsMatch(url, tuplePermission.getUrl());
		
		return httpVerbsAreTheSame && urlsMatch;
	}
}