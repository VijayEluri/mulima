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
package org.mulima.api.file;

import java.io.File;
import java.util.Set;

/**
 * Represents a directory containing files whose values should be cached.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 * @param <T> the type of the file values
 */
public interface CachedDir<T> {
  /**
   * Gets the directory being cached.
   *
   * @return the directory
   */
  File getDir();

  /**
   * Gets all cached files in this directory.
   *
   * @return all cached files
   */
  Set<CachedFile<T>> getCachedFiles();

  /**
   * Gets all files in this directory. Only includes files that have a corresponding cached file.
   *
   * @return all files
   */
  Set<File> getFiles();

  /**
   * Gets the value of all files in this directory. Only includes values for cached files.
   *
   * @return all values
   */
  Set<T> getValues();

  /**
   * Gets the values of all files that are of the type specified in the parameter.
   *
   * @param type the type of values that should be returned
   * @return all values that are instances of the specified type
   */
  <S extends T> Set<S> getValues(Class<S> type);
}
