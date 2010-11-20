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
}
