package org.mulima.future.util;

import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class AsyncCollectors {
    private AsyncCollectors() {
        // do not instantiate
    }

    public static <X, T extends CompletionStage<X>> Collector<T, ?, CompletionStage<List<X>>> resultOfAll() {
        return Collectors.mapping(CompletionStage::toCompletableFuture, Collectors.collectingAndThen(Collectors.toList(), futures -> {
            var all = CompletableFuture.allOf(futures.toArray(size -> new CompletableFuture[size]));
            return all.thenApply(ignored -> {
                return futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList());
            });
        }));
    }

    public static <X, T extends CompletionStage<X>> Collector<T, ?, CompletionStage<Void>> allOf() {
        return Collectors.mapping(CompletionStage::toCompletableFuture, Collectors.collectingAndThen(Collectors.toList(), futures -> {
            return CompletableFuture.allOf(futures.toArray(size -> new CompletableFuture[size]));
        }));
    }
}
