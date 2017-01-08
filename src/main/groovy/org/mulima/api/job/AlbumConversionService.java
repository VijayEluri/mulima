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
package org.mulima.api.job;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.mulima.api.library.LibraryAlbum;

/**
 * A service to convert albums between formats.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface AlbumConversionService {
  /**
   * Submits a conversion from {@code source} to {@code dests}.
   *
   * @param source the source album
   * @param dests the destination albums
   * @return a future representing the execution of the conversion
   */
  Future<Boolean> submit(LibraryAlbum source, Set<LibraryAlbum> dests);

  /** Shuts down the service. */
  void shutdown();

  /**
   * Shuts down the service and waits for termination.
   *
   * @param timeout the maximum time to wait
   * @param unit the time unit of the timeout arg
   */
  void shutdown(long timeout, TimeUnit unit);

  /**
   * Shuts the service down now.
   *
   * @return runnables that were still executing
   */
  List<Runnable> shutdownNow();
}
