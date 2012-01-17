package org.mulima.internal.library.test

import org.mulima.api.file.Digest
import org.mulima.api.file.DigestService
import org.mulima.api.library.Library
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.library.LibraryAlbumFactory
import org.mulima.api.library.LibraryService
import org.mulima.api.library.ReferenceLibrary
import org.mulima.internal.library.DefaultLibraryService

import spock.lang.Specification

class DefaultLibraryServiceSpec extends Specification {
	LibraryService service
	DigestService digestService
	List refLibs
	List destLibs
	
	def setup() {
		digestService = Mock(DigestService)
		ReferenceLibrary refLib1 = Mock(ReferenceLibrary)
		ReferenceLibrary refLib2 = Mock(ReferenceLibrary)
		refLibs = [refLib1, refLib2]
		Library destLib1 = Mock(Library)
		Library destLib2 = Mock(Library)
		Library destLib3 = Mock(Library)
		destLibs = [destLib1, destLib2, destLib3]
		service = new DefaultLibraryService(Mock(LibraryAlbumFactory), digestService)
		service.refLibs = refLibs as Set
		service.destLibs = destLibs as Set
	}
	
	def 'getLibFor returns null if argument is not a subdirectory of any lib root dirs'() {
		given:
		(refLibs + destLibs).each {
			it.rootDir >> Mock(File)
		}
		File dir = Mock(File)
		dir.parentFile >> Mock(File)
		expect:
		service.getLibFor(dir) == null
	}
	
	def 'getLibFor returns library whose root dir is in the hierarchy above the argument'() {
		given:
		(refLibs + destLibs).each {
			it.rootDir >> Mock(File)
		}
		File dir = Mock(File)
		File dir2 = Mock(File)
		dir.parentFile >> dir2
		dir2.parentFile >> refLibs[1].rootDir
		expect:
		service.getLibFor(dir) == refLibs[1]
	}
	
	def 'getAlbumById returns null if the album isn\'t part of any libs'() {
		given:
		LibraryAlbum album = Mock(LibraryAlbum)
		album.id >> new UUID(0L, 1L)
		expect:
		service.getAlbumById(new UUID(0L, 5L)) == null
	}
	
	def 'getAlbumById returns album if it is in one of the libs'() {
		given:
		LibraryAlbum album = Mock(LibraryAlbum)
		UUID id = new UUID(0L, 1L)
		album.id >> id
		destLibs[0].getById(id) >> album
		expect:
		service.getAlbumById(id) == album
	}
	
	def 'isUpToDate returns false if album is out of date'() {
		given:
		LibraryAlbum album = Mock(LibraryAlbum)
		album.digest >> Mock(Digest)
		digestService.create(album) >> Mock(Digest)
		expect:
		!service.isUpToDate(album, false)
	}
	
	def 'isUpToDate if source is out of date returns false only if checkSource is true'() {
		given:
		LibraryAlbum album = Mock(LibraryAlbum)
		Digest digest = Mock(Digest)
		album.digest >> digest
		digestService.create(album) >> digest
		LibraryAlbum source = Mock(LibraryAlbum)
		destLibs[0].getById(_) >> source
		Digest sourceDigest = Mock(Digest)
		source.digest >> sourceDigest
		album.sourceDigest >> sourceDigest
		digestService.create(source) >> Mock(Digest)
		expect:
		!service.isUpToDate(album, true)
		service.isUpToDate(album, false)
	}
	
	def 'isUpToDate returns true if album and source are up to date'() {
		given:
		LibraryAlbum album = Mock(LibraryAlbum)
		Digest digest = Mock(Digest)
		album.digest >> digest
		digestService.create(album) >> digest
		LibraryAlbum source = Mock(LibraryAlbum)
		destLibs[0].getById(_) >> source
		Digest sourceDigest = Mock(Digest)
		source.digest >> sourceDigest
		album.sourceDigest >> sourceDigest
		digestService.create(source) >> sourceDigest
		expect:
		service.isUpToDate(album, true)
		service.isUpToDate(album, false)
	}
}
