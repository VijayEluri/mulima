package org.mulima.api.file;

import org.mulima.api.library.LibraryAlbum;

/**
 * A service that performs digest operations for library albums.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface DigestService {
  /**
   * Creates a digest representing the current state of the files in the given library album.
   *
   * @param libAlbum the library album to create a digest for
   * @return a digest representing the current state
   */
  Digest create(LibraryAlbum libAlbum);

  /**
   * Reads the library album's digest from a file.
   *
   * @param libAlbum the library album
   * @return the digest from the file.
   */
  Digest read(LibraryAlbum libAlbum);

  /**
   * Reads the library album's source digest from a file.
   *
   * @param libAlbum the library album
   * @return the source digest from the file
   */
  Digest readSource(LibraryAlbum libAlbum);

  /**
   * Writes the digest and source digest files for the specified album.
   *
   * @param libAlbum the album to write digests for
   * @param source the source of {@code libAlbum}
   */
  void write(LibraryAlbum libAlbum, LibraryAlbum source);
}
