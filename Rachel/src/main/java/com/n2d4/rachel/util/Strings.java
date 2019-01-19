package com.n2d4.rachel.util;

/**
 * Utility class for string manipulation.
 * <p>
 * Sub-classing is supported.
 * 
 * @author N2D4
 *
 */
public class Strings {
	
	private Strings() {
		Exceptions.privateConstructor();
	}
	
	
	
	
	
	/**
	 * Converts parts of the string to uppercase. This method uses the user's default {@link java.util.Locale} settings.
	 * 
	 * @param s the string.
	 * @param beginIndex the beginning index, inclusive.
	 * @param endIndex the ending index, exclusive.
	 * @return
	 */
	public static String substringToUpperCase(String s, int beginIndex, int endIndex) {
		Requirements.nonNegative(beginIndex, "begin index");
		Requirements.nonNegative(endIndex, "end index");
		
		return s.substring(0, beginIndex) + s.substring(beginIndex, endIndex).toUpperCase() + s.substring(endIndex);
	}
	
	
	/**
	 * Converts parts of the string to lowercase. This method uses the user's default {@link java.util.Locale} settings.
	 * 
	 * @param s the string.
	 * @param beginIndex the beginning index, inclusive.
	 * @param endIndex the ending index, exclusive.
	 * @return
	 */
	public static String substringToLowerCase(String s, int beginIndex, int endIndex) {
		Requirements.nonNegative(beginIndex, "begin index");
		Requirements.nonNegative(endIndex, "end index");
		
		return s.substring(0, beginIndex) + s.substring(beginIndex, endIndex).toLowerCase() + s.substring(endIndex);
	}
}
