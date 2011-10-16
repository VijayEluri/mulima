package x.org.mulima.internal.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.mulima.exception.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.org.mulima.api.file.Digest;
import x.org.mulima.api.library.LibraryAlbum;

public class DigestBuilder {
	private static final Logger logger = LoggerFactory.getLogger(DigestBuilder.class);
	private final LibraryAlbum libAlbum;
	private final Map<File, String> fileToDigest = new HashMap<File, String>();
	
	public DigestBuilder(LibraryAlbum libAlbum) {
		this.libAlbum = libAlbum;
	}
	
	public Digest build() {
		logger.debug("Generating digest of {}", libAlbum.getDir());
		for (File file : libAlbum.getDir().listFiles()) {
			putDigest(file);
		}
		return new DefaultDigest(libAlbum.getId(), fileToDigest);
	}
	
	private void putDigest(File file) {
		fileToDigest.put(file, generateDigest(file));
	}
	
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