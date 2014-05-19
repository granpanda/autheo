package gp.e3.autheo.util;

import java.util.ArrayList;
import java.util.List;

import gp.e3.autheo.authentication.domain.business.TokenFactory;
import gp.e3.autheo.authentication.domain.business.constants.TokenTypes;
import gp.e3.autheo.authentication.domain.entities.Token;
import gp.e3.autheo.authentication.domain.entities.User;
import gp.e3.autheo.authentication.domain.exceptions.TokenGenerationException;

public class TokenFactoryForTests {

	public static Token getDefaultTestToken() {

		Token token = null;

		try {

			User user = UserFactoryForTests.getDefaultTestUser();
			String tokenValue = TokenFactory.getToken(user);
			token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

		} catch (IllegalArgumentException | TokenGenerationException e) {

			e.printStackTrace();
		}

		return token;
	}

	public static Token getDefaultTestToken(int number) {

		Token token = null;

		try {

			User user = UserFactoryForTests.getDefaultTestUser(number);
			String tokenValue = TokenFactory.getToken(user);
			token = new Token(tokenValue, user.getUsername(), user.getOrganizationId(), user.getRoleId(), TokenTypes.TEMPORAL_TOKEN_TYPE.getTypeNumber());

		} catch (IllegalArgumentException | TokenGenerationException e) {

			e.printStackTrace();
		}

		return token;
	}
	
	public static List<Token> getTokenList(int listSize) {
		
		List<Token> tokens = new ArrayList<Token>();
		
		for (int i = 0; i < listSize; i ++) {
			tokens.add(getDefaultTestToken(i));
		}
		
		return tokens;
	}
}