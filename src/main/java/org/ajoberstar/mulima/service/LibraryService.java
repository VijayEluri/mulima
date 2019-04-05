package org.ajoberstar.mulima.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

  public LibraryService(MetadataService metadata, MusicBrainzService musicbrainz, Flac flac, OpusEnc opusenc) {
    this.metadata = metadata;
    this.musicbrainz = musicbrainz;
    this.flac = flac;
    this.opusenc = opusenc;
  }

  public Stream<Album> getSourceAlbums() {
    // TODO implement
    return Stream.empty();
  }

  public boolean isPrepped(Album album) {
    // TODO implement
    return false;
  }

  public Album prepAlbum(Album album) {
    // TODO implement
    return album;
  }

  public boolean isUpToDate(Album album) {

//    var destTime = Stream.of(losslessDir, lossyDir)
//        .map(this::getFileTimes)
//        .flatMap(List::stream)
//        .min(Comparator.naturalOrder()).orElse(FileTime.from(Instant.MIN));
//
//    var sourceTime = getFileTimes(meta.getSourceFile()).stream()
//        .max(Comparator.naturalOrder()).orElse(FileTime.from(Instant.MIN));
//
//    return sourceTime.compareTo(destTime) <= 0;
    return false;
  }

  public void convert(Album album, List<Metadata> metadata, Path losslessRootDir, Path lossyRootDir) {
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
