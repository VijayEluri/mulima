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

import org.mulima.api.file.FileService;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a library album factory.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class DefaultLibraryAlbumFactory implements LibraryAlbumFactory {
  private final FileService fileService;

  /**
   * Constructs a factory that will use the specified file service.
   *
   * @param fileService the file service to use
   */
  @Autowired
  public DefaultLibraryAlbumFactory(FileService fileService) {
    this.fileService = fileService;
  }

  /** {@inheritDoc} */
  @Override
  public LibraryAlbum create(File dir, Library lib) {
    return new DefaultLibraryAlbum(fileService, dir, lib);
  }
}
