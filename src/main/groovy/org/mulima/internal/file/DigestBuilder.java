package org.mulima.internal.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.mulima.api.file.Digest;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.exception.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for creating digests of the
 * current state of a library album.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DigestBuilder {
	private static final Logger logger = LoggerFactory.getLogger(DigestBuilder.class);
	private final LibraryAlbum libAlbum;
	private final Map<File, String> fileToDigest = new HashMap<File, String>();
	
	/**
	 * Creates a new digest builder.
	 * @param libAlbum the library album to
	 * create the builder for
	 */
	public DigestBuilder(LibraryAlbum libAlbum) {
		this.libAlbum = libAlbum;
	}
	
	/**
	 * Builds a digest of the current state of the album.
	 * @return the digest
	 */
	public Digest build() {
		logger.debug("Generating digest of {}", libAlbum.getDir());
		for (File file : libAlbum.getDir().listFiles()) {
			putDigest(file);
		}
		UUID id = libAlbum.getId() == null ? UUID.randomUUID() : libAlbum.getId();
		return new DefaultDigest(id, fileToDigest);
	}
	
	/**
	 * Puts the digest for a specific file in the map.
	 * @param file the file to create a digest of
	 */
	private void putDigest(File file) {
		fileToDigest.put(file, generateDigest(file));
	}
	
	/**
	 * Generates a hash of the file.
	 * @param file the file to generate a digest of
	 * @return a string of the file hash
	 */
	private String generateDigest(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return DigestUtils.shaHex(is);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				logger.warn("Problem closing stream for: {}", file.getAbsolutePath(), e);
			}
		}
	}
}
