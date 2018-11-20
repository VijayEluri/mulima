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
