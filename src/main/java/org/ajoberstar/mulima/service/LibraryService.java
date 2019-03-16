package org.ajoberstar.mulima.service;

import org.ajoberstar.mulima.audio.FlacCodec;
import org.ajoberstar.mulima.audio.OpusEncoder;
import org.ajoberstar.mulima.meta.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  public List<Metadata> lookupChoices(Metadata original) {
    var audioToTracks = original.denormalize().getChildren().stream()
        .collect(Collectors.groupingBy(m -> m.getAudioFile().get()));

    return audioToTracks.entrySet().stream()
        .map(entry -> musicbrainz.calculateDiscId(entry.getValue(), entry.getKey()))
        .flatMap(discId -> musicbrainz.lookupByDiscId(discId).stream())
        .flatMap(disc -> disc.getTagValue("musicbrainz_albumid").stream())
        .distinct()
        .map(musicbrainz::lookupByReleaseId)
        .flatMap(Optional::stream)
        .map(choice -> merge(choice.denormalize(), original.denormalize()))
        .collect(Collectors.toList());
  }

  private Metadata merge(Metadata choice, Metadata original) {
    var builder = Metadata.builder("generic");
    builder.setSourceFile(original.getSourceFile());
    original.getArtworkFile().or(choice::getArtworkFile).ifPresent(builder::setArtworkFile);
    original.getAudioFile().or(choice::getAudioFile).ifPresent(builder::setAudioFile);
    builder.addAllTags(choice.getTags());
    original.getCues().forEach(builder::addCue);
    choice.getChildren().forEach(choiceChild -> {
      var originalChild = original.getChildren().stream()
          .filter(x -> x.getTagValue("discnumber").equals(choiceChild.getTagValue("discnumber")))
          .filter(x -> x.getTagValue("tracknumber").equals(choiceChild.getTagValue("tracknumber")))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException("No matching track for: " + choiceChild + " in " + original));
      builder.addChild(merge(choiceChild, originalChild));
    });
    return builder.build();
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

    var destTime = Stream.of(losslessDir, lossyDir)
        .map(this::getFileTimes)
        .flatMap(List::stream)
        .min(Comparator.naturalOrder()).orElse(FileTime.from(Instant.MIN));

    var sourceTime = getFileTimes(meta.getSourceFile()).stream()
        .max(Comparator.naturalOrder()).orElse(FileTime.from(Instant.MIN));

    if (sourceTime.compareTo(destTime) <= 0) {
      logger.info("{}/{} has already been converted. Skipping.", artist, album);
      return;
    } else {
      try (var files = Files.list(losslessDir)) {
        files.forEach(file -> {
          try {
            Files.delete(file);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        });
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
      try (var files = Files.list(lossyDir)) {
        files.forEach(file -> {
          try {
            Files.delete(file);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        });
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
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
    return metadata.getCommonTagValue(tagName)
      .map(value -> value.replaceAll("[<>:\"\\*\\?\\|/\\\\]+", "_"));
  }

  private List<FileTime> getFileTimes(Path dir) {
    try (var stream = Files.list(dir)) {
      return stream
          .map(file -> {
            try {
              return Files.getLastModifiedTime(file);
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          }).collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public boolean isUpToDate(Metadata album) {
    // TODO implement
    return false;
  }
}
