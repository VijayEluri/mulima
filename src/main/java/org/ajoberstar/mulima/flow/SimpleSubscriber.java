package org.ajoberstar.mulima.flow;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class SimpleSubscriber<T> implements Flow.Subscriber<T>, AutoCloseable {
  private static final Logger logger = LogManager.getLogger(SimpleSubscriber.class);

  private final Counter receivedItems;
  private final Counter processedItems;
  private final Counter failedItems;

  private final String name;
  private final ExecutorService executor;
  private final BlockingQueue<T> buffer;
  private final Consumer<T> itemHandler;
  private final Consumer<? super Throwable> errorHandler;

  private Flow.Subscription subscription;

  public SimpleSubscriber(String name, ExecutorService executor, int maxBufferCapacity, Consumer<T> itemHandler, Consumer<? super Throwable> errorHandler) {
    this.name = name;
    this.executor = executor;
    this.buffer = new ArrayBlockingQueue<>(maxBufferCapacity);
    this.itemHandler = itemHandler;
    this.errorHandler = errorHandler;
    this.subscription = null;

    this.receivedItems = Counter.builder("subscriber.items.received")
        .tag("subscriber.name", name)
        .register(Metrics.globalRegistry);
    this.processedItems = Counter.builder("subscriber.items.processed")
        .tag("subscriber.name", name)
        .register(Metrics.globalRegistry);
    this.failedItems = Counter.builder("subscriber.items.failed")
        .tag("subscriber.name", name)
        .register(Metrics.globalRegistry);
  }

  // Subscriber API

  @Override
  public void onSubscribe(Flow.Subscription subscription) {
    logger.debug("{} received subscription: {}", name, subscription);
    Optional.ofNullable(this.subscription).ifPresent(sub -> {
      throw new IllegalStateException(name + " already subscribed to another publisher.");
    });
    this.subscription = subscription;
    subscription.request(1);
  }

  @Override
  public void onNext(T item) {
    logger.debug("{} received item: {}", name, item);
    receivedItems.increment();
    if (buffer.offer(item)) {
      logger.trace("{} added item to queue: {}", name, item);
      CompletableFuture.completedFuture(buffer.poll())
          .thenAcceptAsync(itemHandler, executor)
          .handle((result, e) -> {
            if (e == null) {
              processedItems.increment();
              logger.debug("{} item successfully processed: {}", name, item);
            } else {
              failedItems.increment();
              logger.error("{} item failed processing: {}", name, item, e);
            }
            subscription.request(1);
            return null;
          });
    } else {
      logger.warn("{} dropping item, as no room on queue: {}", name, item);
    }
  }

  @Override
  public void onError(Throwable throwable) {
    logger.debug("{} received error from publisher.", name, throwable);
    errorHandler.accept(throwable);
  }

  @Override
  public void onComplete() {
    logger.debug("{} received completion from publisher.", name);
    // do nothing
  }

  @Override
  public void close() {
    executor.shutdown();
  }
}
