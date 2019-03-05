package org.mulima.file.audio;

import java.io.File;

import org.mulima.file.FileHolder;

public class ArtworkFile implements FileHolder {
  private final File file;
  private final ArtworkFormat format;

  public ArtworkFile(File file) {
    this.file = file;
    this.format = ArtworkFormat.valueOf(file);
  }

  /**
   * Gets the format of the artwork.
   *
   * @return the format
   */
  public ArtworkFormat getFormat() {
    return format;
  }

  /** {@inheritDoc} */
  @Override
  public File getFile() {
    return file;
  }
}
