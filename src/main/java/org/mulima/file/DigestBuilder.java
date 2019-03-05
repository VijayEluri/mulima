package org.mulima.file;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.library.LibraryAlbum;

/**
 * Builder for creating digests of the current state of a library album.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DigestBuilder {
  private static final Logger logger = LogManager.getLogger(DigestBuilder.class);
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
    Set<DigestEntry> entries = new HashSet<>();
    for (var file : libAlbum.getDir().listFiles()) {
      if (Digest.FILE_NAME.equals(file.getName())
          || Digest.SOURCE_FILE_NAME.equals(file.getName())) {
        continue;
      }
      entries.add(new LiveDigestEntry(file));
    }
    var id = libAlbum.getId() == null ? UUID.randomUUID() : libAlbum.getId();
    return new LazyDigest(id, entries);
  }
}
