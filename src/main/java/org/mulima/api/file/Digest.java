package org.mulima.api.file;

import java.util.Set;
import java.util.UUID;

/**
 * Stores the hashes of a collection of files along with an ID.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface Digest {
  String FILE_NAME = ".digest";
  String SOURCE_FILE_NAME = ".source.digest";

  /**
   * Gets the ID of this digest.
   *
   * @return the ID
   */
  UUID getId();

  /**
   * Gets the digest for a specific file.
   *
   * @param fileName the file to get the hash of
   * @return the digest
   */
  String getDigest(String fileName);

  /**
   * Gets the digest entry for a specific file.
   *
   * @param fileName the file to get the entry for
   * @return the digest entry
   */
  DigestEntry getEntry(String fileName);

  /**
   * Gets all digest entries for this digest.
   *
   * @return the entries
   */
  Set<DigestEntry> getEntries();
}
