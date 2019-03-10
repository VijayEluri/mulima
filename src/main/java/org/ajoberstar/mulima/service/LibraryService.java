package org.ajoberstar.mulima.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import org.ajoberstar.mulima.audio.FlacCodec;
import org.ajoberstar.mulima.audio.OpusEncoder;
import org.ajoberstar.mulima.meta.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LibraryService {
  private static final Logger logger = LogManager.getLogger(LibraryService.class);

  private final MetadataService metadata;
  private final MusicBrainzService musicbrainz;
  private final FlacCodec flac;
  private final OpusEncoder opusenc;

  private Flow.Publisher<Metadata> sourceMetadata;

  public LibraryService(MetadataService metadata, MusicBrainzService musicbrainz, FlacCodec flac, OpusEncoder opusenc) {
    this.metadata = metadata;
    this.musicbrainz = musicbrainz;
    this.flac = flac;
    this.opusenc = opusenc;
  }

  public List<Metadata> scan(Path sourceDir) {
    try (var fileStream = Files.walk(sourceDir)) {
      return fileStream
          .filter(Files::isDirectory)
          .map(metadata::parseDir)
          .filter(m -> !m.getChildren().isEmpty())
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void convert(Metadata album, Path losslessDestDir, Path lossyDestDir) {


  }

  public boolean isUpToDate(Metadata album) {
    // TODO implement
    return false;
  }
}
