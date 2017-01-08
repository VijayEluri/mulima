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
package org.mulima.internal.proc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FutureHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(FutureHandler.class);

  public <T> void handle(String description, Iterable<Future<T>> futures)
      throws InterruptedException {
    Collection<Future<?>> complete = new ArrayList<Future<?>>();
    boolean anyRunning = true;
    while (anyRunning) {
      anyRunning = false;
      for (Future<?> future : futures) {
        if (complete.contains(future)) {
          continue;
        } else if (future.isDone()) {
          try {
            future.get();
          } catch (ExecutionException e) {
            LOGGER.error("{} failed.", description, e);
          }
          complete.add(future);
        } else {
          anyRunning = true;
        }
      }
      Thread.sleep(1000);
    }
  }
}
