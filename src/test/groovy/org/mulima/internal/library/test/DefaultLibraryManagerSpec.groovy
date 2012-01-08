package org.mulima.internal.library.test

import java.util.concurrent.Future;

import org.mulima.api.freedb.FreeDbDao
import org.mulima.api.job.AlbumConversionService
import org.mulima.api.library.Library
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.library.LibraryManager
import org.mulima.api.library.LibraryService
import org.mulima.api.library.ReferenceLibrary
import org.mulima.api.service.MulimaService
import org.mulima.internal.library.DefaultLibraryManager

import spock.lang.Specification

class DefaultLibraryManagerSpec extends Specification {
	LibraryManager manager
	AlbumConversionService conversionService
	FreeDbDao freeDb
	List refLibs
	List destLibs
	Map libToRefToDest
	Map refToDests
	
	def setup() {
		ReferenceLibrary refLib1 = Mock(ReferenceLibrary)
		ReferenceLibrary refLib2 = Mock(ReferenceLibrary)
		refLibs = [refLib1, refLib2]
		
		refLibs.each {
			it.all >> ([mockAlbum(true), mockAlbum(false), mockAlbum(true)] as Set)
		}
		
		Library destLib1 = Mock(Library)
		Library destLib2 = Mock(Library)
		Library destLib3 = Mock(Library)
		destLibs = [destLib1, destLib2, destLib3]
		
		refToDests = (refLib1.all + refLib2.all).inject([:]) { map, ref ->
			map[ref] = [] as Set
			destLibs.each { destLib -> 
				LibraryAlbum dest = Mock()
				dest.lib >> destLib
				destLib.getSourcedFrom(ref) >> dest
				map[ref] << dest
			}
			return map
		}
		
		MulimaService service = Mock()
		LibraryService libService = Mock()
		service.libraryService >> libService
		libService.refLibs >> refLibs
		libService.destLibs >> destLibs
		conversionService = Mock()
		freeDb = Mock()
		manager = new DefaultLibraryManager(service, conversionService, freeDb)
	}
	
	def 'update only converts one library'() {
		given:
		Library lib = destLibs[0]
		when:
		manager.update(lib)
		then:
		interaction {
			(refLibs[0].all + refLibs[1].all).each { ref ->
				def dests = refToDests[ref].findAll { it.lib == lib }
				if (ref.id) {
					1*conversionService.submit(ref, dests) >> mockFuture()
				} else {
					0*conversionService.submit(ref, dests)
				}
			}
		}
	}
	
	def 'updateAll converts all libraries'() {
		when:
		manager.updateAll()
		then:
		interaction {
			(refLibs[0].all + refLibs[1].all).each { ref ->
				if (ref.id) {
					1*conversionService.submit(ref, refToDests[ref]) >> mockFuture()
				} else {
					0*conversionService.submit(ref, refToDests[ref])
				}
			}
		}
	}
	
	def mockFuture() {
		Future future = Mock()
		future.done >> true
		return future
	}
	
	def mockAlbum(boolean hasId) {
		LibraryAlbum album = Mock(LibraryAlbum)
		album.id >> (hasId ? UUID.randomUUID() : null)
		return album
	}
}
