package org.ajoberstar.mulima.audio;

import org.ajoberstar.mulima.service.ProcessResult;
import org.ajoberstar.mulima.service.ProcessService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

public class FlacCodec implements AudioEncoder, AudioDecoder {
    private final String path;
    private final int compressionLevel;
    private final ExecutorService executor;

    public FlacCodec(String path, int compressionLevel, ExecutorService executor) {
        this.path = path;
        this.compressionLevel = compressionLevel;
        this.executor = executor;
    }

    @Override
    public boolean acceptsEncode(Path source, Path destination) {
        return source.getFileName().toString().endsWith(".wav")
                && destination.getFileName().toString().endsWith(".flac");
    }

    @Override
    public boolean acceptsDecode(Path source, Path destination) {
        return source.getFileName().toString().endsWith(".flac")
                && destination.getFileName().toString().endsWith(".wav");
    }

    @Override
    public CompletionStage<Void> encode(Path source, Path destination) {
        return CompletableFuture.supplyAsync(() -> {
            var command = new ArrayList<String>();
            command.add(path);
            command.add("-f");
            command.add("-" + compressionLevel);
            command.add("-o");
            command.add(destination.toString());
            command.add(source.toString());
            return command;
        }).thenComposeAsync(ProcessService::execute, executor)
                .thenAccept(ProcessResult::assertSuccess);
    }

    @Override
    public CompletionStage<Void> decode(Path source, Path destination) {
        return CompletableFuture.supplyAsync(() -> {
            var command = new ArrayList<String>();
            command.add(path);
            command.add("-f");
            command.add("-d");
            command.add("-o");
            command.add(destination.toString());
            command.add(source.toString());
            return command;
        }).thenComposeAsync(ProcessService::execute, executor)
                .thenAccept(ProcessResult::assertSuccess);
    }
}
