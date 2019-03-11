package org.ajoberstar.mulima.flow;

import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Flows {
  private Flows() {
    // do nothing
  }

  public static <T> SubmissionPublisher<T> publisher() {
    return new SubmissionPublisher<>();
  }

  public static <T> SubmissionPublisher<T> publisher(Executor executor, int maxBufferCapacity) {
    return new SubmissionPublisher<>(executor, maxBufferCapacity);
  }

  public static <T> Flow.Subscriber<T> subscriber(String name, Consumer<T> itemAction) {
    return new SimpleSubscriber<>(name, ForkJoinPool.commonPool(), 1, itemAction, t -> {});
  }

  public static <T> Flow.Subscriber<T> subscriber(String name, Consumer<T> itemAction, Consumer<Throwable> errorAction) {
    return new SimpleSubscriber<>(name, ForkJoinPool.commonPool(), 1, itemAction, errorAction);
  }

  public static <T> Flow.Subscriber<T> subscriber(String name, Executor executor, int maxBufferCapacity, Consumer<T> itemAction) {
    return new SimpleSubscriber<>(name, executor, maxBufferCapacity, itemAction, t -> {});
  }

  public static <T> Flow.Subscriber<T> subscriber(String name, Executor executor, int maxBufferCapacity, Consumer<T> itemAction, Consumer<Throwable> errorAction) {
    return new SimpleSubscriber<>(name, executor, maxBufferCapacity, itemAction, errorAction);
  }
}
