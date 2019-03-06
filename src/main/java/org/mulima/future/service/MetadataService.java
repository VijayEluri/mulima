package org.mulima.future.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.future.meta.Metadata;
import org.mulima.future.meta.MetadataParser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MetadataService {
    private static final Logger logger = LogManager.getLogger(MetadataService.class);

    private final List<MetadataParser> parsers;

    public MetadataService(List<MetadataParser> parsers) {
        this.parsers = parsers;
    }

    public Optional<Metadata> parseFile(Path file) {
        return parsers.stream()
                .filter(parser -> parser.accepts(file))
                .map(parser -> parser.parse(file))
                .map(meta -> meta.translate("generic"))
                .findFirst();
    }

    public List<Metadata> parseDir(Path dir) {
        try (var files = Files.list(dir)) {
            return files
                    .map(this::parseFile)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
