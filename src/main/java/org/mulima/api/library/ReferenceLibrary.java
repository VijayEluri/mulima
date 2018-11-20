package org.mulima.api.library;

import java.util.Set;

/**
 * An object representing a library that contains the reference copies of a collection.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface ReferenceLibrary extends Library {
  /**
   * Gets all new albums in this library.
   *
   * @return the new albums
   */
  Set<LibraryAlbum> getNew();
}
