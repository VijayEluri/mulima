package x.org.mulima.internal.file

import java.io.File;
import java.io.IOException;

import x.org.mulima.api.file.Digest
import x.org.mulima.api.file.FileComposer
import x.org.mulima.api.file.FileParser
import x.org.mulima.api.library.LibraryAlbum

class DigestDao implements FileParser<Digest>, FileComposer<Digest> {
	private static final String ID_KEY = "id";
	
	Digest parse(File file) {
		Properties props = new Properties()
		file.withInputStream { it ->
			props.load(it)
		}
		
		UUID id = null
		Map fileToDigest = [:]
		props.each { key, value ->
			if (ID_KEY == key) {
				id = UUID.fromString(value)
			} else {
				File keyFile = new File(file.parentFile, key)
				fileToDigest[keyFile] = value
			}
		}
		return new DefaultDigest(id, fileToDigest)
	}
	
	void compose(File file, Digest digest) {
		if (digest == null) {
			throw new NullPointerException('Digest cannot be null.')
		} else if (digest.id == null) {
			throw new NullPointerException('Digest ID cannot be null.')
		}
		
		Properties props = new Properties()
		props[ID_KEY] = digest.id
		digest.map.each { key, value ->
			props[key.name] = value	
		}
		file.withPrintWriter { writer ->
			props.store(writer, null)	
		}
	}
}
