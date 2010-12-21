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

package com.andrewoberstar.library.util;

/**
 * Contains helper methods for <code>String</code> operations.
 */
public class StringUtil {
	/**
	 * This class should never be instantiated.
	 * @throws UnsupportedOperationException always
	 */
	protected StringUtil() {
		throw new UnsupportedOperationException("Cannot instantiate this class.");
	}
	
	/**
	 * Computes the Levenshtein distance between to <code>String</code>s.
	 * This is computed as described here: {@link http://en.wikipedia.org/wiki/Levenshtein_distance}.
	 * @param arg0 the first <code>String</code>
	 * @param arg1 the second <code>String</code>
	 * @return the Levenshtein distance between the two <code>String</code>s
	 */
	public static int levenshteinDistance(String arg0, String arg1) {
		int[][] dist = new int[arg0.length() + 1][arg1.length() + 1];
		
		for (int i = 0; i < dist.length; i++) {
			dist[i] = new int[arg1.length() + 1];
			dist[i][0] = i;
		}
		
		for (int j = 0; j < dist[0].length; j++) {
			dist[0][j] = j;
		}
		
		for (int j = 1; j < dist[0].length; j++) {
			for (int i = 1; i < dist.length; i++) {
				if (arg0.charAt(i - 1) == arg1.charAt(j - 1)) {
					dist[i][j] = dist[i - 1][j - 1];
				} else {
					int deletion = dist[i - 1][j] + 1;
					int insertion = dist[i][j - 1] + 1;
					int substitution = dist[i - 1][j - 1] + 1;
					
					if (deletion < insertion) {
						if (deletion < substitution) {
							dist[i][j] = deletion;
						} else {
							dist[i][j] = substitution;
						}
					} else {
						if (insertion < substitution) {
							dist[i][j] = insertion;
						} else {
							dist[i][j] = substitution;
						}
					}
				}
			}
		}
		
		return dist[arg0.length()][arg1.length()];
	}
	
	/**
	 * Joins a <code>String[]</code> using <code>glue</code> to separate
	 * the elements of the array.
	 * 
	 * @param strings an array of <code>String</code>s to be joined together
	 * @param glue the <code>String</code> to use in between elements of the array 
	 * @return a <code>String</code> of the combined contents of the array.
	 */
	public static String join(String[] strings, String glue) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < strings.length; i++) {
			builder.append(strings[i]);
			if (i != strings.length - 1) {
				builder.append(glue);
			}
		}
		return builder.toString();
	}
	
	public static String makeSafe(String arg0) {
		return arg0.replaceAll("[\\\\/:\\*\\?\"<>\\|]", "_");
	}
}