package gp.e3.autheo.authorization.domain.entities;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authorization.infrastructure.validators.E3UrlValidator;
import gp.e3.autheo.authorization.infrastructure.validators.HttpVerbValidator;

public class Permission {
	
	private int id;
	private final String name;
	private final String httpVerb;
	private final String url;
	
	public Permission(String name, String httpVerb, String url) {
		
		id = -1;
		this.name = name;
		this.httpVerb = httpVerb;
		this.url = url;
	}
	
	public Permission(int id, String name, String httpVerb, String url) {
		
		this.id = id;
		this.name = name;
		this.httpVerb = httpVerb;
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getHttpVerb() {
		return httpVerb;
	}

	public String getUrl() {
		return url;
	}
	
	public static boolean isValidPermission(Permission permission) {
		
		return (StringValidator.isValidString(permission.getName())) &&
				(HttpVerbValidator.isValidHttpVerb(permission.getHttpVerb())) && 
				(E3UrlValidator.isValidUrl(permission.getUrl()));
	}
}