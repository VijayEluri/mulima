package org.mulima.service;

import org.mulima.audio.tool.ToolService;
import org.mulima.file.DigestService;
import org.mulima.file.FileService;
import org.mulima.file.TempDir;
import org.mulima.library.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of the MulimaService. This simply provides properties for each service.
 * 
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class MulimaService {
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

  public void setTempDir(TempDir tempDir) {
    this.tempDir = tempDir;
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
