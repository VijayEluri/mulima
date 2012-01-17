package org.mulima.internal.file

import org.mulima.api.file.Digest
import org.mulima.api.file.FileComposer
import org.mulima.api.file.FileParser

/**
 * A DAO that parses and composes digest objects
 * to files.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
class DigestDao implements FileParser<Digest>, FileComposer<Digest> {
	private static final String ID_KEY = 'id'
	
	/**
	 * Parses the specified digest file.
	 * @param file the file to parse
	 * @return a digest representing the
	 * file contents
	 */
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
				entries << new StoredDigestEntry(key, value)
			}
		}
		return new LazyDigest(id, entries)
	}
	
	/**
	 * Composes a digest object to a file.
	 * @param file the file to compose to
	 * @param digest the digest to compose
	 */
	void compose(File file, Digest digest) {
		if (digest == null) {
			throw new IllegalArgumentException('Digest cannot be null.')
		} else if (digest.id == null) {
			throw new IllegalArgumentException('Digest ID cannot be null.')
		}
		
		Properties props = new Properties()
		props[ID_KEY] = digest.id.toString()
		digest.entries.each { entry ->
			props[entry.file.name] = entry.toString()
		}
		file.withPrintWriter { writer ->
			props.store(writer, null)	
		}
	}
}
