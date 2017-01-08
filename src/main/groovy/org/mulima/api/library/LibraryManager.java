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
package org.mulima.api.library;

import java.util.Set;

/**
 * An object defining operations to take on a set of libraries.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface LibraryManager {
  /** Updates all albums in all libraries. */
  void updateAll();

  /**
   * Updates all albums in the specified library.
   *
   * @param lib the library to update
   */
  void update(Library lib);

  /**
   * Updates all albums in the specified libraries.
   *
   * @param libs the libraries to update
   */
  void update(Set<Library> libs);
}
