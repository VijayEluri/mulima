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

package com.andrewoberstar.library.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.andrewoberstar.library.util.StringUtil;

public class StringUtilTest {
	
	@Test
	public void levenshteinDistance0() {
		int result = com.andrewoberstar.library.util.StringUtil.levenshteinDistance("kitten", "kitten");
		assertEquals(0, result);
	}
	
	@Test
	public void levenshteinDistance1() {
		int result = StringUtil.levenshteinDistance("kitten", "mitten");
		assertEquals(1, result);
	}
	
	@Test
	public void levenshteinDistance2() {
		int result = StringUtil.levenshteinDistance("toilet", "toil");
		assertEquals(2, result);
	}
	
	@Test
	public void levenshteinDistance3() {
		int result = StringUtil.levenshteinDistance("kitten", "sitting");
		assertEquals(3, result);
	}
	
	@Test
	public void join() {
		String[] values = {"com", "andrewoberstar", "library", "util"};
		String expected = "com.andrewoberstar.library.util";
		assertEquals(expected, StringUtil.join(values, "."));
	}
	
	@Test
	public void makeSafeBackslash() {
		String original = "Testing\\123";
		String expected = "Testing_123";
		assertEquals(expected, StringUtil.makeSafe(original));
	}
	
	@Test
	public void makeSafeSlash() {
		String original = "Testing/123";
		String expected = "Testing_123";
		assertEquals(expected, StringUtil.makeSafe(original));
	}
	
	@Test
	public void makeSafeColon() {
		String original = "Testing:123";
		String expected = "Testing_123";
		assertEquals(expected, StringUtil.makeSafe(original));
	}
	
	@Test
	public void makeSafeQuestion() {
		String original = "Testing?123";
		String expected = "Testing_123";
		assertEquals(expected, StringUtil.makeSafe(original));
	}
	
	@Test
	public void makeSafeAsterisk() {
		String original = "Testing*123";
		String expected = "Testing_123";
		assertEquals(expected, StringUtil.makeSafe(original));
	}
	
	@Test
	public void makeSafeGreater() {
		String original = "Testing>123";
		String expected = "Testing_123";
		assertEquals(expected, StringUtil.makeSafe(original));
	}
	
	@Test
	public void makeSafeLess() {
		String original = "Testing<123";
		String expected = "Testing_123";
		assertEquals(expected, StringUtil.makeSafe(original));
	}
	
	@Test
	public void makeSafePipe() {
		String original = "Testing|123";
		String expected = "Testing_123";
		assertEquals(expected, StringUtil.makeSafe(original));
	}
}
