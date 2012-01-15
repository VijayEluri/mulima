package org.mulima.internal.file.test

import org.mulima.internal.file.StoredDigestEntry

import spock.lang.Specification

class StoredDigestEntrySpec extends Specification {
	String fileName
	long lastModified
	long size
	String digest
	String notation
	
	def setup() {
		fileName = 'temp'
		lastModified = 123456
		size = 1000
		digest = 'kajhakjewhfoawiejf'
		notation = "${lastModified},${size},${digest}"
	}
	
	def 'getModified returens file\'s last modified date'() {
		expect:
		new StoredDigestEntry(fileName, lastModified, size, digest).modified == lastModified
		new StoredDigestEntry(fileName, notation).modified == lastModified
	}
	
	def 'getSize returns file\'s length'() {
		expect:
		new StoredDigestEntry(fileName, lastModified, size, digest).size == size
		new StoredDigestEntry(fileName, notation).size == size
	}
	
	def 'getDigest returns digest of file contents'() {
		expect:
		new StoredDigestEntry(fileName, lastModified, size, digest).digest == digest
		new StoredDigestEntry(fileName, notation).digest == digest
	}
}
