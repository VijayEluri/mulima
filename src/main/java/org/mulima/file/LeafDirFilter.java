package org.mulima.file;

import java.io.File;
import java.io.FileFilter;

import org.mulima.file.audio.AudioFormat;

/**
 * A filter to select only leaf directories.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class LeafDirFilter implements FileFilter {
  /**
   * Only accepts directories that have no child directories.
   *
   * @param file the file to test
   * @return {@code true} if the file is a leaf dir, {@code false} otherwise
   */
  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      boolean anyAudioFiles = false;
      for (File child : file.listFiles()) {
        if (child.isDirectory()) {
          return false;
        } else if (!anyAudioFiles && AudioFormat.isAudioFile(child)) {
          anyAudioFiles = true;
        }
      }
      return anyAudioFiles;
    } else {
      return false;
    }
  }
}
