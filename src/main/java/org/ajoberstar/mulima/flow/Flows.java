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

  public static <T> Flow.Publisher<T> publisher() {
    return new SubmissionPublisher<>();
  }

  public static <T> Flow.Publisher<T> publisher(Executor executor, int maxBufferCapacity) {
    return new SubmissionPublisher<>(executor, maxBufferCapacity);
  }

  public static <T> Flow.Subscriber<T> subscriber(Consumer<T> action) {
    return new SimpleProcessor<>(ForkJoinPool.commonPool(), 1, item -> {
      action.accept(item);
      return null;
    });
  }

  public static <T> Flow.Subscriber<T> subscriber(Executor executor, int maxBufferCapacity, Consumer<T> action) {
    return new SimpleProcessor<>(executor, maxBufferCapacity, item -> {
      action.accept(item);
      return null;
    });
  }

  public static <T, R> Flow.Processor<T, R> subscriber(Function<T, R> function) {
    return new SimpleProcessor<>(ForkJoinPool.commonPool(), 1, function);
  }

  public static <T, R> Flow.Processor<T, R> subscriber(Executor executor, int maxBufferCapacity, Function<T, R> function) {
    return new SimpleProcessor<>(executor, maxBufferCapacity, function);
  }
}
