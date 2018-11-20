package org.mulima.internal.service;

import org.mulima.api.audio.tool.ToolService;
import org.mulima.api.file.DigestService;
import org.mulima.api.file.FileService;
import org.mulima.api.file.TempDir;
import org.mulima.api.library.LibraryService;
import org.mulima.api.service.MulimaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of the MulimaService.  This simply
 * provides properties for each service.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class DefaultMulimaService implements MulimaService {
  private TempDir tempDir = null;
  @Autowired
  private LibraryService libraryService = null;
  @Autowired
  private FileService fileService = null;
  @Autowired
  private DigestService digestService = null;
  @Autowired
  private ToolService toolService = null;

  public TempDir getTempDir() {
    return tempDir;
  }

  public LibraryService getLibraryService() {
    return libraryService;
  }

  public FileService getFileService() {
    return fileService;
  }

  public DigestService getDigestService() {
    return digestService;
  }

  public ToolService getToolService() {
    return toolService;
  }
}
