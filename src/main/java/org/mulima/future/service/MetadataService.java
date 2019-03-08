package org.mulima.future.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.future.meta.Metadata;
import org.mulima.future.meta.MetadataParser;
import org.mulima.future.meta.MetadataWriter;
import org.mulima.future.util.AsyncCollectors;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public final class MetadataService {
    private static final Logger logger = LogManager.getLogger(MetadataService.class);

    private final List<MetadataParser> parsers;
    private final List<MetadataWriter> writers;

    public MetadataService(List<MetadataParser> parsers, List<MetadataWriter> writers) {
        this.parsers = parsers;
        this.writers = writers;
    }

    public Optional<CompletionStage<Metadata>> parseFile(Path file) {
        return parsers.stream()
                .filter(parser -> parser.accepts(file))
                .map(parser -> parser.parse(file))
                .map(meta -> meta.thenApply(m -> m.translate("generic")))
                .findFirst();
    }

    public CompletionStage<List<Metadata>> parseDir(Path dir) {
        try (var files = Files.list(dir)) {
            return files
                    .map(this::parseFile)
                    .flatMap(Optional::stream)
                    .collect(AsyncCollectors.resultOfAll());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public CompletionStage<Void> writeFile(Metadata meta, Path file) {
        return writers.stream()
                .filter(writer -> writer.accepts(file))
                .map(writer -> writer.write(meta, file))
                .findFirst()
                // TODO better
                .orElseThrow(() -> new RuntimeException("No writers for file."));
    }
}
