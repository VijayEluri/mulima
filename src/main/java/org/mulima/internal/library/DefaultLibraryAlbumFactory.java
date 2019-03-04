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