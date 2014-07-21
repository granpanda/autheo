package gp.e3.autheo.authentication.infrastructure.datastructures;

public class Tuple {
	
	private boolean expectedResult;
	private String errorMessage;
	
	public Tuple(boolean expectedResult) {
		
		this.expectedResult = expectedResult;
		errorMessage = "";
	}
	
	public Tuple(String errorMessage) {
		
		expectedResult = false;
		this.errorMessage = errorMessage;
	}

	public Tuple(boolean expectedResult, String errorMessage) {
		
		this.expectedResult = expectedResult;
		this.errorMessage = errorMessage;
	}

	public boolean isExpectedResult() {
		return expectedResult;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}