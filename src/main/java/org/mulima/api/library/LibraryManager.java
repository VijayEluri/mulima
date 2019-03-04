package org.mulima.api.library;

import java.util.Set;

/**
 * An object defining operations to take on a set of libraries.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface LibraryManager {
  /** Process new albums. */
  void processNew();

  /** Updates all albums in all libraries. */
  void updateAll();

  /**
   * Updates all albums in the specified library.
   *
   * @param lib the library to update
   */
  void update(Library lib);

  /**
   * Updates all albums in the specified libraries.
   *
   * @param libs the libraries to update
   */
  void update(Set<Library> libs);
}