package org.ajoberstar.mulima.audio;

import org.ajoberstar.mulima.service.ProcessResult;
import org.ajoberstar.mulima.service.ProcessService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

public class OpusEncoder implements AudioEncoder {
    private final String path;
    private final int bitrate;
    private final ExecutorService executor;

    public OpusEncoder(String path, int bitrate, ExecutorService executor) {
        this.path = path;
        this.bitrate = bitrate;
        this.executor = executor;
    }

    @Override
    public boolean acceptsEncode(Path source, Path destination) {
        return (source.getFileName().toString().endsWith(".wav")
                || source.getFileName().toString().endsWith(".flac"))
                && destination.getFileName().toString().endsWith(".opus");
    }

    @Override
    public CompletionStage<Void> encode(Path source, Path destination) {
        return CompletableFuture.supplyAsync(() -> {
            var command = new ArrayList<String>();
            command.add(path);
            command.add("--bitrate");
            command.add(Integer.toString(bitrate));
            command.add(source.toString());
            command.add(destination.toString());
            return command;
        }).thenComposeAsync(ProcessService::execute, executor)
                .thenAccept(ProcessResult::assertSuccess);
    }
}
