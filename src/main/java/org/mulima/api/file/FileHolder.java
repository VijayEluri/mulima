package org.mulima.api.file;

import java.io.File;

/**
 * Interface describing an object that holds a file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface FileHolder {
  /**
   * Gets the file corresponding to this object.
   *
   * @return the file
   */
  File getFile();
}
