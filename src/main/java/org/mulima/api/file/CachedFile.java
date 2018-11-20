package org.mulima.api.file;

import java.io.File;

/**
 * Represents a file whose parsed value should be cached.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 * @param <T> the type of the file value
 */
public interface CachedFile<T> {
  /**
   * Gets the file being cached.
   *
   * @return the file.
   */
  File getFile();

  /**
   * Gets the parsed value of the file.
   *
   * @return the value
   */
  T getValue();
}
