package org.mulima.library;

import java.io.File;

import org.mulima.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a library album factory.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class LibraryAlbumFactory {
  private final FileService fileService;

  /**
   * Constructs a factory that will use the specified file service.
   *
   * @param fileService the file service to use
   */
  @Autowired
  public LibraryAlbumFactory(FileService fileService) {
    this.fileService = fileService;
  }

  /**
   * Creates a library album using the parameters.
   *
   * @param dir the directory of the album
   * @param lib the library the album belongs in
   * @return a new library album
   */
  public LibraryAlbum create(File dir, Library lib) {
    return new LibraryAlbum(fileService, dir, lib);
  }
}
