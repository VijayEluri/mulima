package org.mulima.internal.file;

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
	public String getDigest(String fileName) {
		DigestEntry entry = getEntry(fileName);
		return entry == null ? null : entry.getDigest();
	}
	
	@Override
	public DigestEntry getEntry(String fileName) {
		for (DigestEntry entry : entries) {
			if (entry.getFileName().equals(fileName)) {
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
