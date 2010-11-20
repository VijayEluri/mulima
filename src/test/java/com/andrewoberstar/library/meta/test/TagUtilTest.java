package com.andrewoberstar.library.meta.test;

import static org.junit.Assert.*;
import org.junit.Test;

import com.andrewoberstar.library.meta.TagUtil;

public class TagUtilTest {
	private String camelCase = "remGenre";
	private String allCaps = "REM_GENRE";
	
	@Test
	public void toCamelCase() {
		String result = TagUtil.toCamelCase(allCaps);
		assertEquals(camelCase, result);
	}
	
	@Test
	public void fromCamelCase() {
		String result = TagUtil.fromCamelCase(camelCase);
		assertEquals(allCaps, result);
	}
}
