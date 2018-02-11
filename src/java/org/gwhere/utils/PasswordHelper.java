package org.gwhere.utils;

public class PasswordHelper {

	private final static Integer ITERATIONS = 2;
	
	public static String generatePassword(String source, String salt){
		return generatePassword(source, salt, ITERATIONS);
	}
	
	public static String generatePassword(String source, String salt, int iterations) {
		return Encodes.encodeHex(Digests.sha1(source.getBytes(), salt.getBytes(), iterations));
	}
}
