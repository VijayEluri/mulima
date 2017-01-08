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
package org.mulima.internal.library;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.mulima.api.library.ReferenceLibrary;

/**
 * Default implementation of a reference library.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DefaultReferenceLibrary extends DefaultLibrary implements ReferenceLibrary {
  /**
   * Constructs a reference library from the parameters.
   *
   * @param libAlbumFactory the factory to create library albums
   * @param name the name of this library
   * @param rootDir the root directory of this library
   * @param format the audio format of the files in this library
   */
  public DefaultReferenceLibrary(
      LibraryAlbumFactory libAlbumFactory, String name, File rootDir, AudioFormat format) {
    super(libAlbumFactory, name, rootDir, format);
  }

  /** {@inheritDoc} */
  @Override
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
