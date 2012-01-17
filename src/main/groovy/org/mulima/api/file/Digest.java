package org.mulima.api.file;

import java.util.Set;
import java.util.UUID;

/**
 * Stores the hashes of a collection of files along
 * with an ID.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Digest {
	static final String FILE_NAME = ".digest";
	String SOURCE_FILE_NAME = ".source.digest";
	
	/**
	 * Gets the ID of this digest.
	 * @return the ID
	 */
	UUID getId();
	
	/**
	 * Gets the digest for a specific file.
	 * @param file the file to get the hash of
	 * @return the digest
	 */
	String getDigest(String fileName);
	
	/**
	 * Gets all digest entries for this digest.
	 * @return the entries
	 */
	Set<DigestEntry> getEntries();
}
