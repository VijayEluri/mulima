package org.mulima.internal.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.mulima.api.file.DigestEntry;
import org.mulima.exception.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveDigestEntry extends AbstractDigestEntry implements DigestEntry {
	private static final Logger logger = LoggerFactory.getLogger(LiveDigestEntry.class);
	private final File file;
	private String digest;
	
	public LiveDigestEntry(File file) {
		this.file = file;
	}
	
	@Override
	public String getFileName() {
		return file.getName();
	}
	
	@Override
	public long getModified() {
		return file.lastModified();
	}
	
	@Override
	public long getSize() { 
		return file.length();
	}
	
	@Override
	public String getDigest() {
		if (digest == null) {
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				digest = DigestUtils.shaHex(is);
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
		return digest;
	}
}
