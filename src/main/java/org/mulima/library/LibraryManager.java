package org.mulima.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.job.AlbumConversionService;
import org.mulima.proc.FutureHandler;
import org.mulima.service.MulimaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a library manager.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class LibraryManager {
  private static final Logger logger = LogManager.getLogger(LibraryManager.class);
  private final MulimaService service;
  private final AlbumConversionService conversionService;

  /**
   * Constructs a library manager from the parameters.
   *
   * @param libraryService the service to use when interacting with the libraries
   * @param conversionService the service to convert albums between formats
   */
  @Autowired
  public LibraryManager(MulimaService service, AlbumConversionService conversionService) {
    this.service = service;
    this.conversionService = conversionService;
  }

  /** Process new albums. */
  public void processNew() {
    service.getLibraryService().getRefLibs().stream()
        .flatMap(refLib -> refLib.getNew().stream())
        .forEach(refAlbum -> service.getDigestService().write(refAlbum, null));
  }

  /** Updates all albums in all libraries. */
  public void updateAll() {
    update(service.getLibraryService().getDestLibs());
  }

  /**
   * Updates all albums in the specified library.
   *
   * @param lib the library to update
   */
  public void update(Library lib) {
    if (!service.getLibraryService().getDestLibs().contains(lib)) {
      throw new IllegalArgumentException(
          "Cannot update a library that doesn't belong to this manager.");
    }
    Set<Library> libs = new HashSet<Library>();
    libs.add(lib);
    update(libs);
  }

  /**
   * Updates all albums in the specified libraries.
   *
   * @param libs the libraries to update
   */
  public void update(Set<Library> libs) {
    try {
      Collection<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
      for (ReferenceLibrary refLib : service.getLibraryService().getRefLibs()) {
        for (LibraryAlbum refAlbum : refLib.getAll()) {
          if (refAlbum.getId() == null) {
            logger.debug("Skipping {}.  It has no ID.", refAlbum.getName());
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
