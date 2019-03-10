package org.ajoberstar.mulima.service;

import com.fasterxml.jackson.core.io.MergedStream;
import org.ajoberstar.mulima.audio.FlacCodec;
import org.ajoberstar.mulima.audio.OpusEncoder;
import org.ajoberstar.mulima.meta.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

public final class LibraryService {
  private static final Logger logger = LogManager.getLogger(LibraryService.class);

  private final MetadataService metadata;
  private final MusicBrainzService musicbrainz;
  private final FlacCodec flac;
  private final OpusEncoder opusenc;
  private final FileMergeService merge;

  private Flow.Publisher<Metadata> sourceMetadata;

  public LibraryService(MetadataService metadata, MusicBrainzService musicbrainz, FlacCodec flac, OpusEncoder opusenc, FileMergeService merge) {
    this.metadata = metadata;
    this.musicbrainz = musicbrainz;
    this.flac = flac;
    this.opusenc = opusenc;
    this.merge = merge;
  }

  public Map<Path, List<Metadata>> scan(Path sourceDir) {
//    var allMetadata = metadata.parseDirRecursive(sourceDir).toCompletableFuture().join();
//    return allMetadata.stream()
//        .collect(Collectors.groupingBy(metadata -> metadata.getSourceFile().getParent()));
    return null;
  }

  public Metadata merge(List<Metadata> metas) {
    var bySourceFileModified = Comparator.<Metadata, FileTime>comparing(m -> {
      try {
        return Files.getLastModifiedTime(m.getSourceFile());
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });

    return metas.stream()
        .sorted(bySourceFileModified)
        .map(meta -> {
          var name = meta.getSourceFile().getFileName().toString();
          var lastDot = name.lastIndexOf('.');
          var prefix = name.substring(0, lastDot);
          var suffix = name.substring(lastDot + 1);
          try {
            var tempFile = Files.createTempFile(prefix + suffix, ".yaml");
            metadata.writeFile(meta, tempFile).toCompletableFuture().join();
            return tempFile;
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        })
        .reduce((left, right) -> {
          try {
            var tempFile = Files.createTempFile("metadata", ".yaml");
            merge.merge(left, right, tempFile).toCompletableFuture().join();
            return tempFile;
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        }).map(metadata::parseFile)
        .orElseThrow(() -> new IllegalArgumentException("No metadata provided to merge."));
  }

  public void convert(Path sourceDir, Path losslessDestDir, Path lossyDestDir) {

  }

  public void handleDirectory(Path albumDir) {
    // try {
    // Files.
    //
    // Optional<CompletionStage<Metadata>> albumXml = Files.list(albumDir)
    // .filter(albumXmlParser::accepts)
    // .map(albumXmlParser::parse)
    // .findAny();
    //
    // Optional<CompletionStage<Metadata>> cueSheet = Files.list(albumDir)
    // .filter(cueSheetParser::accepts)
    // .map(cueSheetParser::parse)
    // .findAny();
    //
    // var artwork = Files.list(albumDir)
    // .
    // } catch (IOException e) {
    // throw new UncheckedIOException(e);
    // }

  }

  public boolean isUpToDate(Metadata album) {
    // TODO implement
    return false;
  }
}
