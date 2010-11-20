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

package com.andrewoberstar.library.meta.dao.test

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

import com.andrewoberstar.library.meta.Album
import com.andrewoberstar.library.meta.dao.impl.AlbumXmlDaoImpl

class AlbumXmlDaoImplTest {
	private Album exampleAlbum
	private File exampleXml
	private File tempXml
	
	@Before
	void prepareAlbums() {
		exampleAlbum = AlbumXmlHelper.getExampleAlbum()
		exampleXml = File.createTempFile("example", ".xml")
		tempXml = File.createTempFile("temp", ".xml")
		AlbumXmlHelper.writeExampleFile(exampleXml);
	}

	@Test
	void read() {
		def album = new AlbumXmlDaoImpl().read(exampleXml)
		assertEquals(exampleAlbum, album)
	}
	
	@Test
	void write() {
		new AlbumXmlDaoImpl().write(tempXml, exampleAlbum)
		
		def temp = []
		tempXml.eachLine { temp.add(it.trim()) }
		
		def example = []
		exampleXml.eachLine { example.add(it.trim()) }
		
		assertEquals(example, temp)
	}
	
	@After
	void cleanup() {
		exampleXml.deleteOnExit()
		tempXml.deleteOnExit()
	}
}
