package org.mulima.file;

import java.io.File;

/**
 * Interface describing the operations of a file parser.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 * @param <T> the type of values to parse
 */
public interface FileParser<T> {
  /**
   * Parses the file's value.
   *
   * @param file the file to parse
   * @return the value of the file
   */
  T parse(File file);
}
