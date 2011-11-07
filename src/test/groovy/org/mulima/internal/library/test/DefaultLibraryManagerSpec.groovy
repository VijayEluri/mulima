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
		
		libToRefToDest = destLibs.inject([:]) { libMap, destLib ->
			libMap[destLib] = (refLib1.all + refLib2.all).inject([:]) { albumMap, ref ->
				LibraryAlbum dest = Mock(LibraryAlbum)
				destLib.getSourcedFrom(ref) >> dest
				albumMap[ref] = dest
				return albumMap
			}
			return libMap
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
			int num = libToRefToDest[lib].size()
			num*service.submit(_, _)
		}
	}
	
	def 'updateAll converts all libraries'() {
		given:
		Map refToDests = destLibs.inject([:]) { map, destLib ->
			libToRefToDest[destLib].each { ref, dest ->
				if (!map.containsKey(ref)) {
					map[ref] = []
				}
				map[ref] << dest
			}
			return map
		}
		when:
		manager.updateAll()
		then:
		interaction {
			int num = refToDests.size()
			num*service.submit(_, _)
		}
	}
}
