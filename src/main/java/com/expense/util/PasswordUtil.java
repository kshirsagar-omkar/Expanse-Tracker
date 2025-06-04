package com.expense.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification
 */

public class PasswordUtil {
	
	//Constants for salt generation
	private static final Integer SALT_LENGTH = 16;
	private static final SecureRandom RANDOM = new SecureRandom();
	
	/**
	 * Generate a secure random salt
	 * 
	 * @return the generated salt as a Base64 encoded string
	 * 
	 */
	public static String generateSalt() {
		byte[] salt = new byte[SALT_LENGTH];
		RANDOM.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}
	
	/**
	 * Hash a password with the provided salt using SHA-256
	 * 
	 * @param password the password to be hashed
	 * @param salt the salt to use
	 * 
	 * @return the hashed password
	 */
	public static String hashPassword(String password, String salt) {
		try {
			byte[] saltBytes = Base64.getDecoder().decode(salt);
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(saltBytes);
			byte[] hashedPassword = md.digest(password.getBytes());
			
			return Base64.getEncoder().encodeToString(hashedPassword);
		}catch(NoSuchAlgorithmException e) {
			throw new RuntimeException("Error Hasing Password", e);
		}
	}
	
	
	
	/**
	 * verify a password against hashed password and salt
	 * 
	 * @param password the password to verify
	 * @param hashedPassword the stored hashed password
	 * @param salt the salt to be use for hashing
	 * 
	 * @return true if the password matches
	 */
	public static Boolean verifyPassword(String password, String hashedPassword, String salt) {
		String hashedInput = hashPassword(password, salt);
		return hashedPassword.equals(hashedInput);
	}
	
	
	/**
	 * Generate a secure password hash (With auto-generated salt)
	 * This is a simplified version that generates the salt internally and returns a formated String
	 * 
	 * @param password the password to be hash
	 * @return a formatted String containing both salt and hash separated by ':'
	 */
	public static String generateSecurePassword(String password) {
		String salt = generateSalt();
		String hash = hashPassword(password, salt);
		return salt + ":" + hash;
	}
	


	/**
	 * Verify a password against a secure formatted password string (salt:hash)
	 * 
	 * @param password the password to be verify
	 * @param securePassword the stored secure password string (salt:hash)
	 * @return true if password matches
	 */
	public static Boolean verifySecuredPassword(String password, String securePassword) {
		String[] parts = securePassword.split(":");
		if(parts.length != 2) {
			return false;
		}
		String salt = parts[0];
		String hash = parts[1];
		
		return verifyPassword(password, hash, salt);
	}
	
	
	/**
     * Check if a password matches a stored password
     * This is an alias for verifySecurePassword to maintain API consistency
     * 
     * @param password the password to check
     * @param storedPassword the stored password (expected to be in format salt:hash)
     * @return true if the password matches
     */
    public static boolean checkPassword(String password, String storedPassword) {
        return verifySecuredPassword(password, storedPassword);
    }

}





















