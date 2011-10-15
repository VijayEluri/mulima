package z.org.mulima.cache

import java.io.File;
import java.io.IOException;

import z.org.mulima.api.file.FileComposer
import z.org.mulima.api.file.FileParser
import z.org.mulima.api.library.LibraryAlbum;

class DigestDao implements FileParser<Digest>, FileComposer<Digest> {
	public static final String DIGEST_NAME = ".digest";
	public static final String SOURCE_DIGEST_NAME = ".source.digest";
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
		return new Digest(id, fileToDigest)
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
	
	Digest read(LibraryAlbum album) {
		return parse(new File(album.getDir(), DIGEST_NAME))
	}
	
	Digest readSource(LibraryAlbum album) {
		return parse(new File(album.getDir(), SOURCE_DIGEST_NAME))
	}
	
	void write(LibraryAlbum album) {
		compose(new File(album.getDir(), DIGEST_NAME), album.getDigest())
	}
	
	void writeSource(LibraryAlbum album) throws IOException {
		compose(new File(album.getDir(), SOURCE_DIGEST_NAME), album.getSourceDigest())
	}
}
