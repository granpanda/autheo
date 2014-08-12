package gp.e3.autheo.authorization.infrastructure.constants;

public enum EnumRoleConstants {
	
	PUBLIC_ROLE("public");

	private String roleName;

	private EnumRoleConstants(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}
}