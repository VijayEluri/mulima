package org.mulima.internal.file.test

import org.mulima.internal.file.LeafDirFilter

import spock.lang.Specification

class LeafDirFilterSpec extends Specification {
	def 'accept returns true for directories with no subdirs'() {
		expect:
		new LeafDirFilter().accept(mockFile(true))
		new LeafDirFilter().accept(mockFile(true, mockFile(false), mockFile(false)))
	}
	
	def 'accept returns false for directories with subdirs'() {
		expect:
		!new LeafDirFilter().accept(mockFile(true, mockFile(false), mockFile(false), mockFile(true)))
		!new LeafDirFilter().accept(mockFile(true, mockFile(true)))
	}
	
	def 'accept returns false for files'() {
		expect:
		!new LeafDirFilter().accept(mockFile(false)) 
	}
	
	def mockFile(boolean isDirectory, File... children) {
		File file = Mock()
		file.directory >> isDirectory
		if (isDirectory && children != null) {
			file.listFiles() >> (children as File[])
		}
		return file
	}
}
