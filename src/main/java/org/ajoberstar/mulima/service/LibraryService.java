package org.ajoberstar.mulima.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.function.Function;
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

  public void convert(Metadata meta, Path losslessDestRootDir, Path lossyDestRootDir) {
    // prep directory names
    var artist = getCommonPathSafeTagValue(meta, "albumartist").or(() -> getCommonPathSafeTagValue(meta, "artist")).orElse("Various Artists");
    var album = getCommonPathSafeTagValue(meta, "album").orElseThrow(() -> new IllegalArgumentException("Unknown album name in: " + meta));

    // create dest directories
    var losslessDir = losslessDestRootDir.resolve(artist).resolve(album);
    var lossyDir = lossyDestRootDir.resolve(artist).resolve(album);

    // FIXME what if they already exist, should we overwrite?
    try {
      Files.createDirectories(losslessDir);
      Files.createDirectories(lossyDir);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    // lossless conversion
    var losslessResult = flac.split(meta, losslessDir);

    // lossy conversion
    losslessResult.getChildren().stream().forEach(track -> {
      var discNum = track.getTagValue("discnumber").map(Integer::parseInt).orElseThrow(() -> new IllegalStateException("Track does not have a disc number: " + track));
      var trackNum = track.getTagValue("tracknumber").map(Integer::parseInt).orElseThrow(() -> new IllegalStateException("Track does not have a track number: " + track));
      var fileName = String.format("D%02dT%02d.opus", discNum, trackNum);
      track.getAudioFile().ifPresent(source -> opusenc.encode(source, lossyDir.resolve(fileName)));
    });
  }

  private Optional<String> getCommonPathSafeTagValue(Metadata metadata, String tagName) {
    var valuesToCount = metadata.getChildren().stream()
        .flatMap(m -> m.getTagValue(tagName).stream())
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    return valuesToCount.entrySet().stream()
        .collect(Collectors.maxBy(Comparator.comparing(Map.Entry::getValue)))
        .map(Map.Entry::getKey);
    // TODO path safe
  }

  public boolean isUpToDate(Metadata album) {
    // TODO implement
    return false;
  }
}
