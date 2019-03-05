package org.mulima.audio.tool;

import org.mulima.file.audio.AudioFile;
import org.mulima.file.audio.AudioFormat;

/**
 * A tagger specifies operations to read and write metadata from an audio file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface Tagger {
  AudioFormat getFormat();

  /**
   * Executes a write operation immediately.
   *
   * @param file the file to write to
   * @return a tagger result
   */
  TaggerResult write(AudioFile file);

  /**
   * Executes a read operation immediately.
   *
   * @param file the file to read from
   * @return a tagger result
   */
  TaggerResult read(AudioFile file);
}
