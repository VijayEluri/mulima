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
