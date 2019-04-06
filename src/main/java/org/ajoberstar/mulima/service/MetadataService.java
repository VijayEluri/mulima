package org.ajoberstar.mulima.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ajoberstar.mulima.meta.Album;
import org.ajoberstar.mulima.meta.CueSheet;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.meta.Metaflac;
import org.ajoberstar.mulima.meta.OpusInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MetadataService {
  private static final Logger logger = LogManager.getLogger(MetadataService.class);

  private final CueSheet cueSheet;
  private final Metaflac metaflac;
  private final OpusInfo opusInfo;

  public MetadataService(CueSheet cueSheet, Metaflac metaflac, OpusInfo opusInfo) {
    this.cueSheet = cueSheet;
    this.metaflac = metaflac;
    this.opusInfo = opusInfo;
  }

  public Optional<Album> parseDir(Path dir) {
    try (var fileStream = Files.list(dir)) {
      var files = fileStream.collect(Collectors.toList());

      var cues = findFiles(files, ".cue")
          .map(file -> {
            var flacName = file.getFileName().toString().replace(".cue", ".flac");
            return Map.entry(file.resolveSibling(flacName), cueSheet.parse(file));
          }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      var flacs = findFiles(files, ".flac")
          .map(file -> Map.entry(file, metaflac.parse(file)));
      var opuses = findFiles(files, ".opus")
          .map(file -> Map.entry(file, opusInfo.parse(file)));
      var meta = Stream.concat(flacs, opuses)
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      var artwork = findFiles(files, ".jpeg", ".jpg", ".png")
          .filter(file -> file.getFileName().toString().startsWith("thumb"))
          .findFirst()
          .map(file -> meta.keySet().stream()
              .collect(Collectors.toMap(Function.identity(), x -> file)))
          .orElse(Map.of());

      return Optional.of(new Album(dir, artwork, cues, meta));
    } catch (Exception e) {
      logger.error("Issue parsing dir: " + dir, e);
      return Optional.empty();
    }
  }

  public void writeMetadata(Path file, Metadata meta) {
    if (file.getFileName().toString().endsWith(".flac")) {
      metaflac.write(meta, file);
    } else {
      throw new IllegalArgumentException("Unsupported file type for writing metadata: " + file);
    }
  }

  private Stream<Path> findFiles(List<Path> dirFiles, String... extensions) {
    return dirFiles.stream()
        .filter(file -> Arrays.stream(extensions)
            .anyMatch(ext -> file.getFileName().toString().endsWith(ext)));
  }
}
