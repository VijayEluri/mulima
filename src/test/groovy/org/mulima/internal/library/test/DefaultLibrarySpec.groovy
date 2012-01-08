package org.mulima.internal.library.test

import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.library.Library
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.library.LibraryAlbumFactory
import org.mulima.api.meta.Album
import org.mulima.api.meta.Disc
import org.mulima.api.meta.test.MetadataFactory
import org.mulima.internal.library.DefaultLibrary
import org.mulima.internal.meta.DefaultAlbum
import org.mulima.internal.meta.DefaultDisc

import spock.lang.Specification

class DefaultLibrarySpec extends Specification {
	Library lib
	Set allLibAlbums
	LibraryAlbum album
	UUID albumId
	UUID albumSourceId
	
	def setup() {
		LibraryAlbum album1 = mockAlbum(new UUID(0L, 1L), null, mockFile(true, mockFile(false), mockFile(false)))
		LibraryAlbum album2 = mockAlbum(new UUID(0L, 2L), new UUID(1L, 1L), mockFile(true, mockFile(false), mockFile(false), mockFile(false)))
		LibraryAlbum album3 = mockAlbum(new UUID(0L, 3L), new UUID(1L, 2L), mockFile(true, mockFile(false)))
		LibraryAlbum album4 = mockAlbum(new UUID(0L, 4L), new UUID(1L, 3L), mockFile(true))
		allLibAlbums = [album1, album2, album3, album4]
		File dir5 = mockFile(true, album1.dir, mockFile(false), album3.dir)
		File dir6 = mockFile(true, mockFile(false), mockFile(false), album2.dir)
		File rootDir = mockFile(true, mockFile(false), dir5, dir6, mockFile(false), mockFile(false), album4.dir)
		
		LibraryAlbumFactory factory = Mock()
		lib = new DefaultLibrary(factory, 'test', rootDir, AudioFormat.WAVE)
		factory.create(_, _) >> { dir, lib -> allLibAlbums.find { it.dir == dir } }
		
		album = album2
		albumId = album2.id
		albumSourceId = album2.sourceId
	}
	
	def 'getAll returns all valid library albums'() {
		expect:
		lib.all == allLibAlbums
	}
	
	def 'getByUUID returns correct album'() {
		expect:
		lib.getById(albumId) == album
	}
	
	def 'getByUUID returns null if the album doesn\'t exist'() {
		expect:
		lib.getById(new UUID(100L, 100L)) == null
	}
	
	def 'getSourcedFrom returns correct album if exists'() {
		lib.getSourcedFrom(mockAlbum(albumSourceId, null, mockFile(true))) == album
	}
	
	def 'getSourcedFrom returns null if doesn\'t exist and createIfNotFound is false'() {
		expect:
		lib.getSourcedFrom(mockAlbum(new UUID(100L, 100L), null, mockFile(true)), false) == null
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
