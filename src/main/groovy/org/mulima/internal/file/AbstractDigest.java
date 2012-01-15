package org.mulima.internal.file;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.Digest;
import org.mulima.api.file.DigestEntry;

public abstract class AbstractDigest implements Digest {
	private final UUID id;
	private Set<DigestEntry> entries;
	
	public AbstractDigest(UUID id, Set<? extends DigestEntry> entries) {
		this.id = id;
		this.entries = Collections.unmodifiableSet(entries);
	}
	
	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getDigest(File file) {
		DigestEntry entry = getEntry(file);
		return entry == null ? null : entry.getDigest();
	}
	
	@Override
	public DigestEntry getEntry(File file) {
		for (DigestEntry entry : entries) {
			if (entry.getFile().equals(file)) {
				return entry;
			}
		}
		return null;
	}
	
	@Override
	public Set<DigestEntry> getEntries() {
		return entries;
	}
}
