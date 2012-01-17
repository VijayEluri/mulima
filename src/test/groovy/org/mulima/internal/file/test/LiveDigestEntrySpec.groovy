package org.mulima.internal.file.test

import org.mulima.internal.file.LiveDigestEntry

import spock.lang.Specification

class LiveDigestEntrySpec extends Specification {
	File file
	long lastModified
	long size
	String digest
	
	def setup() {
		file = File.createTempFile('digest', '.txt')
		file.withPrintWriter { writer ->
			writer.println 'These are the contents of the file.'
		}
		lastModified = file.lastModified()
		size = file.length()
		digest = '43d20799f81b8e5b4a85febdffb07eb2a59f84c6'
	}
	
	def cleanup() {
		assert file.delete()
	}
	
	def 'getModified returens file\'s last modified date'() {
		expect:
		new LiveDigestEntry(file).modified == lastModified
	}
	
	def 'getSize returns file\'s length'() {
		expect:
		new LiveDigestEntry(file).size == size
	}
	
	def 'getDigest returns digest of file contents'() {
		expect:
		new LiveDigestEntry(file).digest == digest
	}
}
