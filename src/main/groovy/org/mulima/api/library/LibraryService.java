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
package org.mulima.api.library;

import java.io.File;
import java.util.Set;
import java.util.UUID;

/**
 * A service that provides information about the libraries currently configured.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface LibraryService {
  /**
   * Gets all reference libraries.
   *
   * @return the reference libraries
   */
  Set<ReferenceLibrary> getRefLibs();

  /**
   * Gets all destination libraries.
   *
   * @return the destination libraries
   */
  Set<Library> getDestLibs();

  /**
   * Gets the library that the specified directory belongs to.
   *
   * @param dir the directory to search for
   * @return the library that {@code dir} belongs to or {@code null} if one can't be found
   */
  Library getLibFor(File dir);

  /**
   * Looks in all libraries for one matching the specified ID.
   *
   * @param id the ID of the album to find
   * @return the album, or {@code null} if one can't be found
   */
  LibraryAlbum getAlbumById(UUID id);

  /**
   * Checks if the specified library is up to date. Will check to see if the source files have
   * changed if {@code checkSource} is set to {@code true}.
   *
   * @param libAlbum the album to check
   * @param checkSource whether or not to check the album's source as well
   * @return {@code true} if the album is up to date, {@code false} otherwise
   */
  boolean isUpToDate(LibraryAlbum libAlbum, boolean checkSource);
}
