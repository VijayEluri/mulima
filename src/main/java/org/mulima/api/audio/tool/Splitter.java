package org.mulima.api.audio.tool;

import java.io.File;

import org.mulima.api.file.audio.DiscFile;

/**
 * A splitter specifies operations for splitting an audio file as specified in a cue sheet.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface Splitter {
  /**
   * Executes a split operation immediately.
   *
   * @param image the file to split
   * @param destDir the destination directory for the files
   * @return a splitter result
   */
  SplitterResult split(DiscFile image, File destDir);
}
