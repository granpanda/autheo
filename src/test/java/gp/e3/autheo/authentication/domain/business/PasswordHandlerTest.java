package gp.e3.autheo.authentication.domain.business;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import gp.e3.autheo.authentication.domain.business.PasswordHandler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PasswordHandlerTest {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGetPasswordHash_OK() {
		
		try {
			
			String password1 = "Hello123!";
			String password2 = "Hola123!Hello";
			
			String passwordHash1 = PasswordHandler.getPasswordHash(password1);
			String passwordHash1Duplicate = PasswordHandler.getPasswordHash(password1);
			
			assertNotEquals(0, passwordHash1.length());
			assertNotEquals(0, passwordHash1Duplicate.length());
			
			// The length should be the same for every hash.
			assertEquals(passwordHash1.length(), passwordHash1Duplicate.length());
			/* 
			 * The hashes should not be equal, even if the given parameters are the same
			 * because the salt is generated randomly. 
			 */
			assertNotEquals(passwordHash1, passwordHash1Duplicate);
			
			String passwordHash2 = PasswordHandler.getPasswordHash(password2);
			assertNotEquals(0, passwordHash2.length());
			
			// The length should be the same for every hash.
			assertEquals(passwordHash1.length(), passwordHash2.length());
			/* 
			 * The hashes should not be equal, even if the given parameters are the same
			 * because the salt is generated randomly. 
			 */
			assertNotEquals(passwordHash1, passwordHash2);
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetPasswordHash_NOK() {
		
		try {
			
			String emptyPassword = "";
			PasswordHandler.getPasswordHash(emptyPassword);
			
			fail("The method should throw an exception because the given password was empty.");
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
			
			assertNotNull(e);
		}
	}
	
	@Test
	public void testGetSaltFromHashedAndSaltedPassword_OK() {
		
		int numberOfIterations = 123;
		String saltHash = "qwe";
		String passwordHash = "wer";
		
		String hashedAndSaltedPassword = numberOfIterations + PasswordHandler.SPLIT_TOKEN + saltHash +
				PasswordHandler.SPLIT_TOKEN + passwordHash; 
		
		String saltFromPasswordHandler = PasswordHandler.getSaltFromHashedAndSaltedPassword(hashedAndSaltedPassword);
		
		assertEquals(saltHash, saltFromPasswordHandler);
	}
	
	@Test
	public void testGetSaltFromHashedAndSaltedPassword_NOK_1() {
		
		try {
			
			int numberOfIterations = 123;
			String saltHash = "qwe";
			String passwordHash = "wer";
			
			String invalidFormat = numberOfIterations + saltHash + passwordHash; 
			PasswordHandler.getSaltFromHashedAndSaltedPassword(invalidFormat);
			
			fail("The method should throw an exception because the given argument was in an invalid format.");
			
		} catch (IllegalArgumentException e) {
			
			assertNotNull(e);
		}
	}
	
	@Test
	public void testGetSaltFromHashedAndSaltedPassword_NOK_2() {
		
		try {
			
			String invalidFormat = null; 
			PasswordHandler.getSaltFromHashedAndSaltedPassword(invalidFormat);
			
			fail("The method should throw an exception because the given argument was null.");
			
		} catch (IllegalArgumentException e) {
			
			assertNotNull(e);
		}
	}
	
	@Test
	public void testValidatePassword_OK() {
		
		try {
			
			String password = "Hola123!Hello";
			String passwordHash = PasswordHandler.getPasswordHash(password);
			
			assertEquals(true, PasswordHandler.validatePassword(password, passwordHash));
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testValidatePassword_NOK_1() {
		
		try {
			
			String password = "Hola123!Hello";
			String passwordHash = "qweqweqweqweqwe";
			
			PasswordHandler.validatePassword(password, passwordHash);
			
			fail("The method should throw an exception because the passwordHash does not follow the expected pattern");
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
			
			assertNotNull(e);
		}
	}
	
	@Test
	public void testValidatePassword_NOK_2() {
		
		try {
			
			String password = "Hola123!Hello";
			String passwordHash = null;
			
			PasswordHandler.validatePassword(password, passwordHash);
			
			fail("The method should throw an exception because the passwordHash is null");
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
			
			assertNotNull(e);
		}
	}
}