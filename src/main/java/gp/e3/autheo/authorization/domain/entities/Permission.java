package gp.e3.autheo.authorization.domain.entities;

import gp.e3.autheo.authentication.infrastructure.validators.StringValidator;
import gp.e3.autheo.authorization.infrastructure.validators.E3UrlValidator;
import gp.e3.autheo.authorization.infrastructure.validators.HttpVerbValidator;

public class Permission implements Comparable<Permission> {
	
	public static final String ATTRIBUTE_SPLIT = ":";
	public static final String PERMISSION_SPLIT = ";";
	
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
	
	@Override
	public String toString() {
		
		return id + ATTRIBUTE_SPLIT + name + ATTRIBUTE_SPLIT + httpVerb + ATTRIBUTE_SPLIT + url;
	}

	@Override
	public int compareTo(Permission permission) {
		
		int answer = 0;
		
		answer += this.name.compareTo(permission.getName());
		answer += this.httpVerb.compareTo(permission.getHttpVerb());
		answer += this.url.compareTo(permission.getUrl());
		
		return answer;
	}
}