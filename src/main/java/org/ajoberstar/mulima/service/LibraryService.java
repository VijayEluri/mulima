package org.ajoberstar.mulima.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ajoberstar.mulima.audio.Flac;
import org.ajoberstar.mulima.audio.OpusEnc;
import org.ajoberstar.mulima.meta.Album;
import org.ajoberstar.mulima.meta.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LibraryService {
  private static final Logger logger = LogManager.getLogger(LibraryService.class);

  private final MetadataService metadata;
  private final MusicBrainzService musicbrainz;
  private final Flac flac;
  private final OpusEnc opusenc;
  private final Path sourceRootDir;
  private final Path losslessRootDir;
  private final Path lossyRootDir;

  public LibraryService(MetadataService metadata, MusicBrainzService musicbrainz, Flac flac, OpusEnc opusenc, Map<String, Path> libraries) {
    this.metadata = metadata;
    this.musicbrainz = musicbrainz;
    this.flac = flac;
    this.opusenc = opusenc;
    this.sourceRootDir = libraries.get("source");
    this.losslessRootDir = libraries.get("lossless");
    this.lossyRootDir = libraries.get("lossy");
  }

  public List<Album> getSourceAlbums() {
    try (var stream = Files.walk(sourceRootDir)) {
      return stream
          .filter(Files::isRegularFile)
          .map(Path::getParent)
          .distinct()
          .map(metadata::parseDir)
          .filter(album -> !album.getAudioToCues().isEmpty())
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public boolean isPrepped(Album album) {
    return album.getAudioToMetadata().entrySet().stream()
        .allMatch(entry -> entry.getValue().getTagValue("musicbrainz_releaseid").isPresent());
  }

  public Album prepAlbum(Album album) {
    // TODO implement
    return album;
  }

  public void convert(Album album, List<Metadata> metadata, boolean force) {
    // prep directory names
    var artistName = metadata.stream()
        .findAny()
        .flatMap(meta -> meta.getTagValue("albumartist"))
        .map(this::toPathSafe)
        .orElseThrow(() -> new IllegalArgumentException("Album must have albumartist: " + album.getDir()));
    var albumName = metadata.stream()
        .findAny()
        .flatMap(meta -> meta.getTagValue("album"))
        .map(this::toPathSafe)
        .orElseThrow(() -> new IllegalArgumentException("Album must have album: " + album.getDir()));

    // create dest directories
    var losslessDir = losslessRootDir.resolve(artistName).resolve(albumName);
    var lossyDir = lossyRootDir.resolve(artistName).resolve(albumName);

    if (!force && isUpToDate(album, metadata, losslessDir, lossyDir)) {
      // TODO log
      return;
    }

    emptyDir(losslessDir);
    emptyDir(lossyDir);

    // lossless conversion
    var losslessResult = flac.split(album, metadata, losslessDir);

    // lossy conversion
    losslessResult.stream().forEach(losslessFile -> {
      var lossyFile = lossyDir.resolve(losslessFile.getFileName().toString().replace(".flac", ".opus"));
      opusenc.encode(losslessFile, lossyFile);
    });
  }

  private boolean isUpToDate(Album album, List<Metadata> metadata, Path losslessDir, Path lossyDir) {
    var destTime = Stream.of(getFileTimes(losslessDir), getFileTimes(lossyDir))
        .flatMap(List::stream)
        .min(Comparator.naturalOrder())
        .orElse(FileTime.from(Instant.MIN));

    var sourceTime = getFileTimes(album.getDir()).stream()
        .max(Comparator.naturalOrder())
        .orElse(FileTime.from(Instant.MIN));

    return sourceTime.compareTo(destTime) < 0;
  }

  private String toPathSafe(String value) {
    return value.replaceAll("[<>:\"\\*\\?\\|/\\\\]+", "_");
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

  private void emptyDir(Path dir) {
    try {
      if (Files.exists(dir)) {
        Files.list(dir).forEach(file -> {
          try {
            Files.delete(file);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        });
      } else {
        Files.createDirectories(dir);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
