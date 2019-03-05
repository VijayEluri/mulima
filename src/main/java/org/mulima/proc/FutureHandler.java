package org.mulima.proc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FutureHandler {
  private static final Logger logger = LogManager.getLogger(FutureHandler.class);

  public <T> void handle(String description, Iterable<Future<T>> futures)
      throws InterruptedException {
    Collection<Future<?>> complete = new ArrayList<>();
    var anyRunning = true;
    while (anyRunning) {
      anyRunning = false;
      for (Future<?> future : futures) {
        if (complete.contains(future)) {
        } else if (future.isDone()) {
          try {
            future.get();
          } catch (ExecutionException e) {
            logger.error("{} failed.", description, e);
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
