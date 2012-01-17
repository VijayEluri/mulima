package org.mulima.internal.file.test

import java.util.List

import org.mulima.api.file.CachedDir
import org.mulima.api.file.CachedFile
import org.mulima.api.file.FileService
import org.mulima.internal.file.DefaultCachedDir

import spock.lang.Specification

class DefaultCachedDirSpec extends Specification {
	FileService service = Mock(FileService)
	File dir = mockFile(false)
	FileFilter filter
	Map fileToValue = [:]
	
	def setup() {
		filter = new FileFilter() {
			boolean accept(File pathname) {
				return pathname.name.endsWith('flac')
			}
		}
		
		dir.directory >> true
		dir.listFiles() >> ([mockFile(true), mockFile(true), mockFile(false), mockFile(true)] as File[])
		dir.listFiles(null) >> dir.listFiles()
		dir.listFiles(filter) >> dir.listFiles().findAll { filter.accept(it) }
		dir.lastModified() >> 100L
		dir.listFiles().each {
			CachedFile file = Mock(CachedFile)
			service.createCachedFile(Object, it) >> file
			file.file >> it
			Object value = Mock(Object)
			file.value >> value
			fileToValue[it] = value
		}
	}
	
	def 'getCachedFiles without filter creates cached files for all children'() {
		given:
		CachedDir cache = new DefaultCachedDir(service, Object, dir)
		expect:
		cache.cachedFiles*.file as Set == dir.listFiles() as Set
	}
	
	def 'getCachedFiles with filter creates cached files only for children that match'() {
		given:
		CachedDir cache = new DefaultCachedDir(service, Object, dir, filter)
		expect:
		cache.cachedFiles*.file as Set == dir.listFiles(filter) as Set
	}
	
	def 'getFiles without filter returns files for all children'() {
		given:
		CachedDir cache = new DefaultCachedDir(service, Object, dir)
		expect:
		cache.files == dir.listFiles() as Set
	}
	
	def 'getFiles with filter only returns children that match'() {
		given:
		CachedDir cache = new DefaultCachedDir(service, Object, dir, filter)
		expect:
		cache.files == dir.listFiles(filter) as Set
	}
	
	def 'getValues without filter returns values for all children'() {
		given:
		CachedDir cache = new DefaultCachedDir(service, Object, dir)
		expect:
		cache.values == fileToValue.values() as Set
	}
	
	def 'getValues with filter only returns children that match'() {
		given:
		CachedDir cache = new DefaultCachedDir(service, Object, dir, filter)
		Set values = fileToValue.inject([] as Set) { set, key, value ->
			if (filter.accept(key)) {
				set << value
			}
			return set
		}
		expect:
		cache.values == values 
	}
	
	def mockFile(boolean audioFile) {
		File file = Mock(File)
		file.name >> (audioFile ? 'test.flac' : 'test.txt')
		file.compareTo(_) >> { file.is(it) ? 0 : 1 }
		return file
	}
}
