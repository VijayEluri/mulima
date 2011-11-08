package org.mulima.internal.library.test

import org.mulima.api.audio.AudioFormat
import org.mulima.api.job.AlbumConversionService
import org.mulima.api.library.Library
import org.mulima.api.library.LibraryAlbum
import org.mulima.api.library.LibraryManager
import org.mulima.api.library.LibraryService
import org.mulima.api.library.ReferenceLibrary
import org.mulima.internal.library.DefaultLibraryManager

import spock.lang.Specification

class DefaultLibraryManagerSpec extends Specification {
	LibraryManager manager
	AlbumConversionService service
	List refLibs
	List destLibs
	Map libToRefToDest
	Map refToDests
	
	def setup() {
		ReferenceLibrary refLib1 = Mock(ReferenceLibrary)
		ReferenceLibrary refLib2 = Mock(ReferenceLibrary)
		refLibs = [refLib1, refLib2]
		
		refLibs.each {
			it.all >> ([Mock(LibraryAlbum), Mock(LibraryAlbum)] as Set)
		}
		
		Library destLib1 = Mock(Library)
		Library destLib2 = Mock(Library)
		Library destLib3 = Mock(Library)
		destLibs = [destLib1, destLib2, destLib3]
		
		refToDests = (refLib1.all + refLib2.all).inject([:]) { map, ref ->
			map[ref] = [] as Set
			destLibs.each { destLib -> 
				LibraryAlbum dest = Mock(LibraryAlbum)
				dest.lib >> destLib
				destLib.getSourcedFrom(ref) >> dest
				map[ref] << dest
			}
			return map
		}
		
		LibraryService libService = Mock(LibraryService)
		libService.refLibs >> refLibs
		libService.destLibs >> destLibs
		service = Mock(AlbumConversionService)
		manager = new DefaultLibraryManager(libService, service)
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
				1*service.submit(ref, dests)
			}
		}
	}
	
	def 'updateAll converts all libraries'() {
		when:
		manager.updateAll()
		then:
		interaction {
			(refLibs[0].all + refLibs[1].all).each { ref ->
				1*service.submit(ref, refToDests[ref])	
			}
		}
	}
}