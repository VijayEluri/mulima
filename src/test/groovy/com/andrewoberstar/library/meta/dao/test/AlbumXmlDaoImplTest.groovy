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
