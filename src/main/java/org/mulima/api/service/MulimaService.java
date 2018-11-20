package org.mulima.api.service;

import org.mulima.api.audio.tool.ToolService;
import org.mulima.api.file.DigestService;
import org.mulima.api.file.FileService;
import org.mulima.api.file.TempDir;
import org.mulima.api.library.LibraryService;

public interface MulimaService {
  TempDir getTempDir();

  LibraryService getLibraryService();

  FileService getFileService();

  DigestService getDigestService();

  ToolService getToolService();
}
