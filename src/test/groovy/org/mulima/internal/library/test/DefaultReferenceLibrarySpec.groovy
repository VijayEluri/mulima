package org.mulima.internal.library.test

import java.io.File
import java.util.Set
import java.util.UUID

import org.mulima.api.audio.AudioFormat
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.library.LibraryAlbumFactory
import org.mulima.api.library.ReferenceLibrary
import org.mulima.internal.library.DefaultReferenceLibrary

import spock.lang.Specification

class DefaultReferenceLibrarySpec extends Specification {
	ReferenceLibrary lib
	Set newLibAlbums
	
	def setup() {
		LibraryAlbum album1 = mockAlbum(null, null, mockFile(true, mockFile(false), mockFile(false)))
		LibraryAlbum album2 = mockAlbum(new UUID(0L, 2L), new UUID(1L, 1L), mockFile(true, mockFile(false), mockFile(false), mockFile(false)))
		LibraryAlbum album3 = mockAlbum(null, new UUID(1L, 2L), mockFile(true, mockFile(false)))
		LibraryAlbum album4 = mockAlbum(new UUID(0L, 4L), new UUID(1L, 3L), mockFile(true))
		def allLibAlbums = [album1, album2, album3, album4]
		newLibAlbums = [album1, album3]
		File dir5 = mockFile(true, album1.dir, mockFile(false), album3.dir)
		File dir6 = mockFile(true, mockFile(false), mockFile(false), album2.dir)
		File rootDir = mockFile(true, mockFile(false), dir5, dir6, mockFile(false), mockFile(false), album4.dir)
		
		LibraryAlbumFactory factory = Mock()
		lib = new DefaultReferenceLibrary(factory, 'test', rootDir, AudioFormat.WAVE)
		factory.create(_, _) >> { dir, lib -> allLibAlbums.find { it.dir == dir } }
	}
	
	def 'getNew returns albums with no ID assigned'() {
		expect:
		lib.new == newLibAlbums
	}
	
	def mockAlbum(UUID id, UUID sourceId, File dir) {
		LibraryAlbum album = Mock(LibraryAlbum)
		album.id >> id
		album.sourceId >> sourceId
		album.dir >> dir
		return album
	}
	
	def mockFile(boolean isDirectory, File... children = null) {
		File file = Mock(File)
		file.directory >> isDirectory
		file.path >> UUID.randomUUID().toString()
		if (isDirectory) {
			file.listFiles() >> (children ?: [] as File[])
		}
		file.compareTo(_) >> { file.is(it) ? 0 : 1 }
		return file
	}
}