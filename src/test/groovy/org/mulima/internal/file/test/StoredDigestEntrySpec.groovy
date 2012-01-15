package org.mulima.internal.file.test

import org.mulima.internal.file.StoredDigestEntry

import spock.lang.Specification

class StoredDigestEntrySpec extends Specification {
	File file
	long lastModified
	long size
	String digest
	String notation
	
	def setup() {
		file = Mock()
		lastModified = 123456
		size = 1000
		digest = 'kajhakjewhfoawiejf'
		notation = "${lastModified},${size},${digest}"
	}
	
	def 'getModified returens file\'s last modified date'() {
		expect:
		new StoredDigestEntry(file, lastModified, size, digest).modified == lastModified
		new StoredDigestEntry(file, notation).modified == lastModified
	}
	
	def 'getSize returns file\'s length'() {
		expect:
		new StoredDigestEntry(file, lastModified, size, digest).size == size
		new StoredDigestEntry(file, notation).size == size
	}
	
	def 'getDigest returns digest of file contents'() {
		expect:
		new StoredDigestEntry(file, lastModified, size, digest).digest == digest
		new StoredDigestEntry(file, notation).digest == digest
	}
}
