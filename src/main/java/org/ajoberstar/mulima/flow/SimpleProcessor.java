package org.ajoberstar.mulima.flow;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

class SimpleProcessor<T, R> implements Flow.Processor<T, R> {
  private final Executor executor;
  private final BlockingQueue<T> buffer;
  private final Function<T, R> function;
  private final SubmissionPublisher<R> publisher;

  private Flow.Subscription subscription;

  public SimpleProcessor(Executor executor, int maxBufferCapacity, Function<T, R> function) {
    this.executor = executor;
    this.buffer = new ArrayBlockingQueue<>(maxBufferCapacity);
    this.function = function;
    this.publisher = new SubmissionPublisher<>();
    this.subscription = null;
  }

  // Processor API

  @Override public void subscribe(Flow.Subscriber<? super R> subscriber) {
    publisher.subscribe(subscriber);
  }

  // Subscriber API

  @Override public void onSubscribe(Flow.Subscription subscription) {
    Optional.ofNullable(this.subscription).ifPresent(sub -> {
      throw new IllegalStateException("Already subscribed to another publisher.");
    });
    this.subscription = subscription;
    subscription.request(buffer.remainingCapacity());
  }

  @Override public void onNext(T item) {
    if (buffer.offer(item)) {
      CompletableFuture.completedFuture(buffer.remove())
          .thenApply(function)
          .thenApply(publisher::submit)
          .thenRun(() -> subscription.request(1))
          .exceptionally(e -> {
            publisher.closeExceptionally(e);
            return null;
          });
    } else {
      // drop items that we don't have room for
    }
  }

  @Override public void onError(Throwable throwable) {
    // do nothing
  }

  @Override public void onComplete() {
    // do nothing
  }
}
