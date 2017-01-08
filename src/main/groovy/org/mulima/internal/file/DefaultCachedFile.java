/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mulima.internal.file;

import java.io.File;

import org.mulima.api.file.CachedFile;
import org.mulima.api.file.FileParser;

/**
 * Default implementation of a cached file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 * @param <T> the type of the file's value
 */
public class DefaultCachedFile<T> implements CachedFile<T> {
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
  public DefaultCachedFile(FileParser<T> parser, File file) {
    this.parser = parser;
    this.file = file;
  }

  /** {@inheritDoc} */
  @Override
  public File getFile() {
    return file;
  }

  /** {@inheritDoc} */
  @Override
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
