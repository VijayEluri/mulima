package org.mulima.api.file;

import java.io.File;
import java.util.Map;
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
	String getDigest(File file);
	
	/**
	 * Gets a map of files to digest strings.
	 * @return the map
	 */
	Map<File, String> getMap();
}
