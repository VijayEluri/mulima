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
package org.mulima.internal.file;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.Digest;
import org.mulima.api.file.DigestEntry;
import org.mulima.api.library.LibraryAlbum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for creating digests of the current state of a library album.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DigestBuilder {
  private static final Logger logger = LoggerFactory.getLogger(DigestBuilder.class);
  private final LibraryAlbum libAlbum;

  /**
   * Creates a new digest builder.
   *
   * @param libAlbum the library album to create the builder for
   */
  public DigestBuilder(LibraryAlbum libAlbum) {
    this.libAlbum = libAlbum;
  }

  /**
   * Builds a digest of the current state of the album.
   *
   * @return the digest
   */
  public Digest build() {
    logger.debug("Generating digest of {}", libAlbum.getDir());
    if (!libAlbum.getDir().exists()) {
      throw new IllegalArgumentException("Album directory does not exists: " + libAlbum.getDir());
    }
    Set<DigestEntry> entries = new HashSet<DigestEntry>();
    for (File file : libAlbum.getDir().listFiles()) {
      if (Digest.FILE_NAME.equals(file.getName())
          || Digest.SOURCE_FILE_NAME.equals(file.getName())) {
        continue;
      }
      entries.add(new LiveDigestEntry(file));
    }
    UUID id = libAlbum.getId() == null ? UUID.randomUUID() : libAlbum.getId();
    return new LazyDigest(id, entries);
  }
}
