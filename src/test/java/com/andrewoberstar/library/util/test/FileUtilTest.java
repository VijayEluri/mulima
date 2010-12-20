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
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.andrewoberstar.library.util.FileUtil;

public class FileUtilTest {
	@Test
	public void baseName() {
		File file = new File("Temp" + File.separator + "testPotatoe.txt");
		assertEquals("testPotatoe", FileUtil.getBaseName(file));
	}
	
	@Test
	public void changeExtension() {
		File file = new File("Temp" + File.separator + "testPotatoe.txt");
		File temp = FileUtil.changeExtension(file, "cue");
		assertEquals("Temp" + File.separator + "testPotatoe.cue", temp.getPath());
	}
	
	@Test
	public void listDirsRecursiveSuccess() {
		File[] child4 = { mockFile(false, null), mockFile(false, null)};
		File dir4 = mockFile(true, child4);
		
		File[] child3 = { mockFile(false, null), dir4, mockFile(false, null)};
		File dir3 = mockFile(true, child3);
		
		File[] child2 = { mockFile(false, null) };
		File dir2 = mockFile(true, child2);
		
		File[] child1 = { mockFile(false, null), dir2, dir3 };
		File dir1 = mockFile(true, child1);
		
		List<File> expected = new ArrayList<File>();
		expected.add(dir1);
		expected.add(dir2);
		expected.add(dir3);
		expected.add(dir4);
		
		assertEquals(expected, FileUtil.listDirsRecursive(dir1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void listDirsRecursiveException() {
		File file = mockFile(false, null);
		FileUtil.listDirsRecursive(file);
	}
	
	private File mockFile(boolean isDirectory, File[] children) {
		File file = mock(File.class);
		when(file.isDirectory()).thenReturn(isDirectory);
		if (isDirectory && children != null) {
			when(file.listFiles()).thenReturn(children);
		}
		return file;
	}
}
