package org.mulima.file;

import java.io.File;

/**
 * Default implementation of a cached file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 * @param <T> the type of the file's value
 */
public class CachedFile<T> {
  private final FileParser<T> parser;
  private final File file;
  private long lastRefreshed = 0;
  private T cached;

  /**
   * Creates a cached file whose value will be retrieved using the specified parser.
   *
   * @param parser the parser that will work on this file.
   * @param file the file to cache
   */
  public CachedFile(FileParser<T> parser, File file) {
    this.parser = parser;
    this.file = file;
  }

  /**
   * Gets the file being cached.
   *
   * @return the file.
   */
  public File getFile() {
    return file;
  }

  /**
   * Gets the parsed value of the file.
   *
   * @return the value
   */
  public T getValue() {
    long lastModified = file.lastModified();
    if (lastModified == lastRefreshed) {
      return cached;
    } else {
      T value = parser.parse(file);
      cached = value;
      lastRefreshed = lastModified;
      return cached;
    }
  }
}
