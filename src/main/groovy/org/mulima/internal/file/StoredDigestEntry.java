package org.mulima.internal.file;

import java.io.File;

import org.mulima.api.file.DigestEntry;

public class StoredDigestEntry extends AbstractDigestEntry implements DigestEntry {
	private final File file;
	private final long modified;
	private final long size;
	private final String digest;
	
	public StoredDigestEntry(File file, String notation) {
		this.file = file;
		String[] parts = notation.split(",", 3);
		if (parts.length < 3) {
			throw new IllegalArgumentException("Invalid digest entry notation: " + notation);
		}
		this.modified = Long.valueOf(parts[0]);
		this.size = Long.valueOf(parts[1]);
		this.digest = parts[2];
	}
	
	public StoredDigestEntry(File file, long modified, long size, String digest) {
		this.file = file;
		this.modified = modified;
		this.size = size;
		this.digest = digest;
	}
	
	@Override
	public File getFile() {
		return file;
	}

	@Override
	public long getModified() {
		return modified;
	}

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public String getDigest() {
		return digest;
	}
}
