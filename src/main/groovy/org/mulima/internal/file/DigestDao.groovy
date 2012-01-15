package org.mulima.internal.file

import org.mulima.api.file.Digest
import org.mulima.api.file.FileComposer
import org.mulima.api.file.FileParser

class DigestDao implements FileParser<Digest>, FileComposer<Digest> {
	private static final String ID_KEY = 'id'
	
	Digest parse(File file) {
		Properties props = new Properties()
		file.withInputStream { it ->
			props.load(it)
		}
		
		UUID id = null
		Set entries = [] as Set
		props.each { key, value ->
			if (ID_KEY == key) {
				id = UUID.fromString(value)
			} else {
				if (value.contains(',')) {
					entries << new StoredDigestEntry(key, value)
				} else {
					entries << new StoredDigestEntry(key, "0,0,${value}")
				}
			}
		}
		return new LazyDigest(id, entries)
	}
	
	void compose(File file, Digest digest) {
		if (digest == null) {
			throw new IllegalArgumentException('Digest cannot be null.')
		} else if (digest.id == null) {
			throw new IllegalArgumentException('Digest ID cannot be null.')
		}
		
		Properties props = new Properties()
		props[ID_KEY] = digest.id.toString()
		digest.entries.each { entry ->
			props[entry.file.name] = entry
		}
		file.withPrintWriter { writer ->
			props.store(writer, null)	
		}
	}
}
