package org.mulima.internal.file.test

import org.mulima.api.file.DigestEntry
import org.mulima.internal.file.LiveDigestEntry
import org.mulima.internal.file.StoredDigestEntry

import spock.lang.Specification

class AbstractDigestEntrySpec extends Specification {
	DigestEntry live
	DigestEntry store
	DigestEntry storeM
	DigestEntry storeS
	DigestEntry storeD
	
	def setup() {
		File file = File.createTempFile('digest', '.txt')
		file.withPrintWriter { writer ->
			writer.println 'These are the contents of the file.'
		}
		long lastModified = file.lastModified()
		long size = file.length()
		String digest = '43d20799f81b8e5b4a85febdffb07eb2a59f84c6'
		live = new LiveDigestEntry(file)
		store = new StoredDigestEntry(file.name, lastModified, size, digest)
		storeM = new StoredDigestEntry(file.name, 0, size, digest)
		storeS = new StoredDigestEntry(file.name, lastModified, 0, digest)
		storeD = new StoredDigestEntry(file.name, lastModified, size, '')
	}
	
	def 'equals returns false only if digest is different'() {
		expect:
		live == live
		store == store
		live == store
		store == live
		store == storeM
		storeM == store
		live == storeM
		storeM == live
		store == storeS
		storeS == store
		live == storeS
		storeS == live
		store != storeD
		storeD != store
		live != storeD
		storeD != live
	}
	
	def 'lazyEquals returns true if modified and size are the same'() {
		expect:
		live.lazyEquals(live)
		store.lazyEquals(store)
		live.lazyEquals(store)
		store.lazyEquals(live)
		store.lazyEquals(storeD)
		storeD.lazyEquals(store)
		live.lazyEquals(storeD)
		storeD.lazyEquals(live)
	}
	
	def 'lazyEquals returns true if modifed or size is different but digest is the same'() {
		expect:
		store.lazyEquals(storeM)
		storeM.lazyEquals(store)
		live.lazyEquals(storeM)
		storeM.lazyEquals(live)
		store.lazyEquals(storeS)
		storeS.lazyEquals(store)
		live.lazyEquals(storeS)
		storeS.lazyEquals(live)
	}
	
	def 'lazyEquals returns dalse if all three attributes are different'() {
		given:
		def storeA = new StoredDigestEntry(null, 0, 0, '')
		expect:
		!store.lazyEquals(storeA)
		!storeA.lazyEquals(store)
	}
}
