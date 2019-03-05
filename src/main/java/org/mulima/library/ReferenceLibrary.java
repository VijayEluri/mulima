package org.mulima.library;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.mulima.file.audio.AudioFormat;

/**
 * Default implementation of a reference library.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class ReferenceLibrary extends Library {
  /**
   * Constructs a reference library from the parameters.
   *
   * @param libAlbumFactory the factory to create library albums
   * @param name the name of this library
   * @param rootDir the root directory of this library
   * @param format the audio format of the files in this library
   */
  public ReferenceLibrary(
      LibraryAlbumFactory libAlbumFactory, String name, File rootDir, AudioFormat format) {
    super(libAlbumFactory, name, rootDir, format);
  }

  /**
   * Gets all new albums in this library.
   *
   * @return the new albums
   */
  public Set<LibraryAlbum> getNew() {
    Set<LibraryAlbum> newAlbums = new HashSet<LibraryAlbum>();
    for (LibraryAlbum libAlbum : getAll()) {
      if (libAlbum.getId() == null) {
        newAlbums.add(libAlbum);
      }
    }
    return Collections.unmodifiableSet(newAlbums);
  }
}
