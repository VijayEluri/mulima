package org.mulima.file.audio;

import java.io.File;

/**
 * A base implementation of an audio file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public abstract class AbstractAudioFile implements AudioFile {
  private final File file;
  private final AudioFormat format;

  /**
   * Constructs an audio file from the parameters.
   *
   * @param file the file
   */
  public AbstractAudioFile(File file) {
    this.file = file;
    this.format = AudioFormat.valueOf(file);
  }

  /** {@inheritDoc} */
  @Override
  public File getFile() {
    return file;
  }

  /** {@inheritDoc} */
  @Override
  public AudioFormat getFormat() {
    return format;
  }

  @Override
  public String toString() {
    return file.toString();
  }
}
