package org.mulima.internal.file;

import org.mulima.api.file.DigestEntry;

public class StoredDigestEntry extends AbstractDigestEntry implements DigestEntry {
	private final String fileName;
	private final long modified;
	private final long size;
	private final String digest;
	
	public StoredDigestEntry(String fileName, String notation) {
		this.fileName = fileName;
		String[] parts = notation.split(",", 3);
		if (parts.length < 3) {
			throw new IllegalArgumentException("Invalid digest entry notation: " + notation);
		}
		this.modified = Long.valueOf(parts[0]);
		this.size = Long.valueOf(parts[1]);
		this.digest = parts[2];
	}
	
	public StoredDigestEntry(String fileName, long modified, long size, String digest) {
		this.fileName = fileName;
		this.modified = modified;
		this.size = size;
		this.digest = digest;
	}
	
	@Override
	public String getFileName() {
		return fileName;
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
