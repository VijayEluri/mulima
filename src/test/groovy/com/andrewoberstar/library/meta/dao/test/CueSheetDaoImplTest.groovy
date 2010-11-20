package com.andrewoberstar.library.meta.dao.test

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

import com.andrewoberstar.library.meta.CueSheet
import com.andrewoberstar.library.meta.dao.impl.CueSheetDaoImpl

class CueSheetDaoImplTest {
	private CueSheet exampleCue
	private File exampleFile
	private File tempFile
	
	@Before
	void prepareCues() {
		exampleCue = CueSheetHelper.getExampleCue()
		exampleFile = File.createTempFile("example", ".cue")
		tempFile = File.createTempFile("temp", ".cue")
		CueSheetHelper.writeExampleFile(exampleFile);
	}

	@Test
	void read() {
		def cue = new CueSheetDaoImpl().read(exampleFile)
		assertEquals(exampleCue, cue)
	}
	
	@Test
	void write() {
		new CueSheetDaoImpl().write(tempFile, exampleCue)
		
		def temp = []
		tempFile.eachLine { temp.add(it.trim()) }
		
		def example = []
		exampleFile.eachLine { example.add(it.trim()) }
		
		assertEquals(example, temp)
	}
	
	@After 
	void cleanup() {
		exampleFile.deleteOnExit()
		tempFile.deleteOnExit()
	}
}
