package org.mulima.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.mulima.api.audio.AudioFile;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.meta.CueSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigestBuilder {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final LibraryAlbum libAlbum;
	private final Map<File, String> fileToDigest = new HashMap<File, String>();
	
	public DigestBuilder(LibraryAlbum libAlbum) {
		this.libAlbum = libAlbum;
	}
	
	public Digest build() throws IOException {
		logger.info("Generating digest of {}", libAlbum.getDir());
		if (libAlbum.getAlbum() != null && libAlbum.getAlbum().getFile().getParentFile().equals(libAlbum.getDir())) {
			putDigest(libAlbum.getAlbum().getFile());
		}
		for (CueSheet cue : libAlbum.getCues()) {
			putDigest(cue.getFile());
		}
		for (AudioFile file : libAlbum.getAudioFiles()) {
			putDigest(file);
		}
		return new Digest(libAlbum.getId(), fileToDigest);
	}
	
	private void putDigest(File file) throws IOException {
		fileToDigest.put(file, generateDigest(file));
	}
	
	private String generateDigest(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		try {
			return DigestUtils.shaHex(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.warn("Problem closing stream for: {}", file.getAbsolutePath(), e);
			}
		}
	}
}
