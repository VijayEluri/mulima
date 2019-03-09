package org.ajoberstar.mulima.util;

import java.net.http.HttpClient;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class HttpClients {
  private static final Logger logger = LogManager.getLogger(HttpClients.class);

  private HttpClients() {
    // don't instantiate
  }

  public static HttpClient.Builder rateLimitedBuilder(long minDelayMillis) {
    return HttpClient.newBuilder()
        .executor(new RateLimitExecutor(minDelayMillis, 0));
  }

  public static HttpClient rateLimited(long minDelayMillis) {
    return rateLimitedBuilder(minDelayMillis).build();
  }

  private static class RateLimitExecutor implements Executor {
    private final long delayMillis;
    private final int delayNanos;
    private final Executor delegate;

    public RateLimitExecutor(long delayMillis, int delayNanos) {
      this.delayMillis = delayMillis;
      this.delayNanos = delayNanos;
      this.delegate = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(Runnable command) {
      delegate.execute(() -> {
        // TODO lower level
        logger.error("Executing task...");

        command.run();

        try {
          Thread.sleep(delayMillis, delayNanos);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          // TODO better
        }
      });
    }
  }
}
