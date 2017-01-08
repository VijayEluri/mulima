/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
