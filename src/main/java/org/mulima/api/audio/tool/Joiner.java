package org.mulima.api.audio.tool;

import java.util.List;

import org.mulima.api.file.audio.AudioFile;

/**
 * A joiner specifies operations for joining audio files into a single file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface Joiner {
  /**
   * Execute a join operation immediately.
   *
   * @param files the files to join
   * @param dest the destination file
   * @return a joiner result
   */
  JoinerResult join(List<AudioFile> files, AudioFile dest);
}
