package com.andrewoberstar.library.meta;

/**
 * Contains helper methods for tag operations.
 */
public class TagUtil {
	/**
	 * This class should never be instantiated.
	 * @throws UnsupportedOperationException always
	 */
	protected TagUtil() {
		throw new UnsupportedOperationException("Cannot instantiate this class.");
	}
	
	/**
	 * Converts a tag name to camel case.  The tag name must be in
	 * all uppercase letters, with underscores separating words.
	 * @param value the tag name to convert
	 * @return the camel case version of <code>value</code>
	 */
	public static String toCamelCase(String value) {
		StringBuilder builder = new StringBuilder();
		boolean underscorePrev = false;
		
		for (char ch : value.toCharArray()) {
			if (ch == '_') {
				underscorePrev = true;
			} else {
				builder.append(underscorePrev ? ch : Character.toLowerCase(ch));
				underscorePrev = false;
			}				
		}
		return builder.toString();
	}
	
	/**
	 * Converts a camel case tag name to uppercase.  The tag name
	 * must be in all lowercase, with uppercase first letters in
	 * each word after the first.
	 * @param value the camel case tag name to convert
	 * @return the uppercase version of <code>value</code>
	 */
	public static String fromCamelCase(String value) {
		StringBuilder builder = new StringBuilder();
		
		for (char ch : value.toCharArray()) {
			if (Character.isUpperCase(ch)) {
				builder.append("_" + ch);
			} else {
				builder.append(Character.toUpperCase(ch));
			}				
		}
		return builder.toString();
	}
}
