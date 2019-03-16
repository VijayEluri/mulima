package org.ajoberstar.mulima.flow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;

public final class Flows {
  private Flows() {
    // do nothing
  }

  public static <T> SubmissionPublisher<T> publisher(String name, int maxBufferCapacity) {
    var executor = newExecutorService(name, 1);
    return new SubmissionPublisher<>(executor, maxBufferCapacity);
  }

  public static <T> Flow.Subscriber<T> subscriber(String name, int maxBufferCapacity, Consumer<T> itemAction) {
    var executor = newExecutorService(name, maxBufferCapacity);
    return new SimpleSubscriber<>(name, executor, maxBufferCapacity, itemAction, t -> {
    });
  }

  public static <T> Flow.Subscriber<T> subscriber(String name, int maxBufferCapacity, Consumer<T> itemAction, Consumer<Throwable> errorAction) {
    var executor = newExecutorService(name, maxBufferCapacity);
    return new SimpleSubscriber<>(name, executor, maxBufferCapacity, itemAction, errorAction);
  }

  public static ExecutorService newExecutorService(String name, int poolSize) {
    var threadFactory = new NamingThreadFactory(name);
    var executor = Executors.newFixedThreadPool(poolSize, threadFactory);
    ExecutorServiceMetrics.monitor(Metrics.globalRegistry, executor, name);
    return executor;
  }

  private static class NamingThreadFactory implements ThreadFactory {
    private final String name;
    private AtomicInteger number;

    public NamingThreadFactory(String name) {
      this.name = name;
      this.number = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(Runnable r) {
      var threadName = String.format("%s-pool-%d", name, number.incrementAndGet());
      var thread = new Thread(r, threadName);
      thread.setDaemon(true);
      return thread;
    }
  }
}
