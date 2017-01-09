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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.mulima.api.job.AlbumConversionService;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryManager;
import org.mulima.api.library.ReferenceLibrary;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CuePoint;
import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Track;
import org.mulima.api.service.MulimaService;
import org.mulima.internal.meta.DefaultAlbum;
import org.mulima.internal.proc.FutureHandler;
import org.mulima.util.MetadataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a library manager.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class DefaultLibraryManager implements LibraryManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLibraryManager.class);
  private final MulimaService service;
  private final AlbumConversionService conversionService;

  /**
   * Constructs a library manager from the parameters.
   *
   * @param libraryService the service to use when interacting with the libraries
   * @param conversionService the service to convert albums between formats
   */
  @Autowired
  public DefaultLibraryManager(MulimaService service, AlbumConversionService conversionService) {
    this.service = service;
    this.conversionService = conversionService;
  }

  /** {@inheritDoc} */
  @Override
  public void processNew() {
      service.getLibraryService().getRefLibs().stream()
        .flatMap(refLib -> refLib.getNew().stream())
        .forEach(refAlbum -> service.getDigestService().write(refAlbum, null));
  }

  /** {@inheritDoc} */
  @Override
  public void updateAll() {
    update(service.getLibraryService().getDestLibs());
  }

  /** {@inheritDoc} */
  @Override
  public void update(Library lib) {
    if (!service.getLibraryService().getDestLibs().contains(lib)) {
      throw new IllegalArgumentException(
          "Cannot update a library that doesn't belong to this manager.");
    }
    Set<Library> libs = new HashSet<Library>();
    libs.add(lib);
    update(libs);
  }

  /** {@inheritDoc} */
  @Override
  public void update(Set<Library> libs) {
    try {
      Collection<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
      for (ReferenceLibrary refLib : service.getLibraryService().getRefLibs()) {
        for (LibraryAlbum refAlbum : refLib.getAll()) {
          if (refAlbum.getId() == null) {
            LOGGER.debug("Skipping {}.  It has no ID.", refAlbum.getName());
            continue;
          }
          Set<LibraryAlbum> destAlbums = new HashSet<LibraryAlbum>();
          for (Library destLib : libs) {
            destAlbums.add(destLib.getSourcedFrom(refAlbum));
          }
          futures.add(conversionService.submit(refAlbum, destAlbums));
        }
      }
      new FutureHandler().handle("Conversion", futures);
    } catch (InterruptedException e) {
      conversionService.shutdownNow();
      Thread.currentThread().interrupt();
    } finally {
      conversionService.shutdown(5, TimeUnit.SECONDS);
    }
  }
}
