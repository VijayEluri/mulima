package org.mulima.api.file;

import java.util.Set;
import java.util.UUID;

public interface Digest {
	static final String FILE_NAME = ".digest";
	String SOURCE_FILE_NAME = ".source.digest";
	UUID getId();	
	String getDigest(String fileName);
	DigestEntry getEntry(String fileName);
	Set<DigestEntry> getEntries();
}
