package gp.e3.autheo.authentication.domain.business.constants;

public enum TokenTypes {
	
	TEMPORAL_TOKEN_TYPE (1, "Temporal token"),
	API_KEY_TOKEN_TYPE (2, "Api Token"),
	INTERNAL_API_TOKEN_TYPE (3, "Internal token");
	
	private int typeNumber;
	private String typeName;
	
	private TokenTypes(int typeNumber, String typeName) {
		
		this.typeNumber = typeNumber;
		this.typeName = typeName;
	}

	public int getTypeNumber() {
		return typeNumber;
	}

	public String getTypeName() {
		return typeName;
	}
}