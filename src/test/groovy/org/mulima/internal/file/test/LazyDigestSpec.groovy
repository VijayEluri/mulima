package org.mulima.internal.file.test

import org.mulima.internal.file.LazyDigest
import org.mulima.internal.file.StoredDigestEntry

import spock.lang.Specification

class LazyDigestSpec extends Specification {	
	def 'equals returns true if ids are the same and digests are lazily equal'() {
		given:
		UUID id = UUID.randomUUID()
		File file1 = Mock()
		File file2 = Mock()
		def entries = [new StoredDigestEntry(file1, 0, 0, ''), new StoredDigestEntry(file2, 1000, 2000, 'the')] as Set
		def entries2 = [new StoredDigestEntry(file1, 0, 0, ''), new StoredDigestEntry(file2, 1000, 2000, 'te')] as Set
		def entries3 = [new StoredDigestEntry(file1, 0, 1000, ''), new StoredDigestEntry(file2, 1000, 2000, 'the')] as Set
		def entries4 = [new StoredDigestEntry(file1, 100, 0, 'a'), new StoredDigestEntry(file2, 1000, 2000, 'the')] as Set
		expect:
		new LazyDigest(id, entries) == new LazyDigest(id, entries)
		new LazyDigest(id, entries2) == new LazyDigest(id, entries2)
		new LazyDigest(id, entries3) == new LazyDigest(id, entries3)
		new LazyDigest(id, entries4) == new LazyDigest(id, entries4)
		new LazyDigest(id, entries) == new LazyDigest(id, entries2)
		new LazyDigest(id, entries) == new LazyDigest(id, entries3)
		new LazyDigest(id, entries) != new LazyDigest(id, entries4)
	}
}
