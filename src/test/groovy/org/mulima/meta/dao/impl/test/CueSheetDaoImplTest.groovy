/*
*  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
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
package org.mulima.meta.dao.impl.test

import java.io.File

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mulima.api.meta.CueSheet
import org.mulima.meta.dao.impl.CueSheetDaoImpl

class CueSheetDaoImplTest {
	private CueSheet exampleCue
	private File exampleFile
	private File tempFile
	
	@Before
	void prepareCues() {
		exampleCue = CueSheetHelper.exampleCue
		exampleFile = File.createTempFile('example', '.cue')
		tempFile = File.createTempFile('temp', '.cue')
		CueSheetHelper.writeExampleFile(exampleFile)
	}

	@Test
	void read() {
		def cue = new CueSheetDaoImpl().read(exampleFile)
		assert exampleCue == cue
	}
	
//	@Test
//	void write() {
//		new CueSheetDaoImpl().write(tempFile, exampleCue)
//		
//		def temp = []
//		tempFile.eachLine { temp.add(it.trim()) }
//		
//		def example = []
//		exampleFile.eachLine { example.add(it.trim()) }
//		
//		assert example == temp
//	}
	
	@After 
	void cleanup() {
		exampleFile.deleteOnExit()
		tempFile.deleteOnExit()
	}
}
