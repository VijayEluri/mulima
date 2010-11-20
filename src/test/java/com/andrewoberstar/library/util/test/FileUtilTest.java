package com.andrewoberstar.library.util.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

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
}
