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
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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
      Function<Album, String> toReleaseId = album -> album.getAudioToMetadata().values().stream()
          .flatMap(meta -> meta.getTagValue("musicbrainz_releaseid").stream())
          // make sure they're all the same
          .reduce((a, b) -> {
            if (a.equals(b)) {
              return a;
            } else {
              return "Unknown";
            }
          })
          .orElse("Unknown");

      var albums = stream
          .filter(Files::isRegularFile)
          .map(Path::getParent)
          .distinct()
          .map(metadata::parseDir)
          .flatMap(Optional::stream)
          .filter(album -> !album.getAudioToCues().isEmpty())
          .collect(Collectors.groupingBy(toReleaseId, Collectors.toList()));

      return albums.entrySet().stream().flatMap(group -> {
        if ("Unknown".equals(group.getKey())) {
          return group.getValue().stream();
        } else {
          var dir = group.getValue().stream()
              .map(Album::getDir)
              .findFirst()
              .orElseThrow(() -> new AssertionError("No albums in group."));
          var artwork = group.getValue().stream()
              .map(Album::getArtwork)
              .flatMap(List::stream)
              .collect(Collectors.toList());
          var cues = group.getValue().stream()
              .map(Album::getAudioToCues)
              .map(Map::entrySet)
              .flatMap(Set::stream)
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
          var meta = group.getValue().stream()
              .map(Album::getAudioToMetadata)
              .map(Map::entrySet)
              .flatMap(Set::stream)
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

          return Stream.of(new Album(dir, artwork, cues, meta));
        }
      }).sorted(Comparator.comparing(Album::getDir))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public boolean isPrepped(Album album) {
    return album.getAudioToMetadata().values().stream()
        .allMatch(meta -> meta.getTagValue("musicbrainz_releaseid").isPresent());
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
    if (!Files.exists(dir)) {
      return List.of();
    }
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
