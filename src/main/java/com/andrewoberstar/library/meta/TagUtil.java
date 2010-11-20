/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
