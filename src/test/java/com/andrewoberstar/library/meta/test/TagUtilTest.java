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
