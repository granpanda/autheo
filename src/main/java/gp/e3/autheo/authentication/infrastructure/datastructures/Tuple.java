package gp.e3.autheo.authentication.infrastructure.datastructures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

	@JsonCreator
	public Tuple(@JsonProperty("expectedResult") boolean expectedResult, @JsonProperty("errorMessage") String errorMessage) {
		
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