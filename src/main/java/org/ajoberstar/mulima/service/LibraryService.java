package org.ajoberstar.mulima.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
              .map(Album::getAudioToArtwork)
              .map(Map::entrySet)
              .flatMap(Set::stream)
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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

  public Optional<List<Metadata>> findMetadata(Album album) {
    var releaseId = album.getAudioToMetadata().values().stream()
        .flatMap(meta -> meta.getTagValue("musicbrainz_releaseid").stream())
        .findAny()
        .orElseThrow(() -> new IllegalStateException("Album does not have release ID: " + album.getDir()));

    return musicbrainz.lookupByReleaseId(releaseId).map(tracks -> {
      Function<Metadata, Integer> toDiscNum = meta -> meta.getTagValue("discnumber").map(Integer::parseInt).orElse(-1);
      var discToAudio = album.getAudioToMetadata().entrySet().stream()
          .collect(Collectors.toMap(entry -> toDiscNum.apply(entry.getValue()), Map.Entry::getKey));

      return tracks.stream().map(track -> {
        var discNum = toDiscNum.apply(track);
        var audio = discToAudio.get(discNum);
        var artwork = album.getAudioToArtwork().get(audio);
        return track.copy()
            .setArtwork(artwork)
            .build();
      }).collect(Collectors.toList());
    });
  }

  public void convert(Album album, List<Metadata> metadata, boolean force) {
    // prep directory names
    var artistName = metadata.stream()
        .findAny()
        .flatMap(meta -> meta.getTagValue("albumartistsort"))
        .map(this::toPathSafe)
        .orElseThrow(() -> new IllegalArgumentException("Album must have albumartist: " + album.getDir()));
    var albumName = metadata.stream()
        .findAny()
        .flatMap(meta -> meta.getTagValue("album"))
        .map(this::toPathSafe)
        .orElseThrow(() -> new IllegalArgumentException("Album must have album: " + album.getDir()));
    var releaseDate = metadata.stream()
        .findAny()
        .flatMap(meta -> meta.getTagValue("releasedate"))
        .map(d -> String.format(" (%s)", d))
        .orElse("");

    // create dest directories
    var losslessDir = losslessRootDir.resolve(artistName).resolve(albumName + releaseDate);
    var lossyDir = lossyRootDir.resolve(artistName).resolve(albumName + releaseDate);

    if (!force && isUpToDate(album, metadata, losslessDir, lossyDir)) {
      logger.debug("Album is up-to-date: {}", album.getDir());
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

  private boolean isUpToDate(Album album, List<Metadata> tracks, Path losslessDir, Path lossyDir) {
    // check audio length
    var accurateSplit = album.getAudioToCues().entrySet().stream().allMatch(entry -> {
      var audio = entry.getKey();
      var cues = entry.getValue();

      var trackFrames = IntStream.range(0, cues.size() - 2 + 1)
          .mapToObj(start -> cues.subList(start, start + 2))
          .map(pair -> pair.get(1).getOffset() - pair.get(0).getOffset())
          .collect(Collectors.toList());

      var discNum = album.getAudioToMetadata().get(audio).getTagValue("discnumber")
          .map(Integer::parseInt)
          .orElseThrow(() -> new IllegalArgumentException("No discnumber found for: " + audio));

      return IntStream.rangeClosed(1, trackFrames.size()).allMatch(trackNum -> {
        var flacFile = losslessDir.resolve(String.format("D%03dT%02d.flac", discNum, trackNum));
        var opusFile = lossyDir.resolve(String.format("D%03dT%02d.opus", discNum, trackNum));

        if (Files.exists(flacFile) && Files.exists(opusFile)) {
          var targetFrames = trackFrames.get(trackNum - 1);
          var flacFrames = metadata.getTotalFrames(flacFile);
          var opusFrames = metadata.getTotalFrames(opusFile);

          if (targetFrames == flacFrames && targetFrames == opusFrames) {
            return true;
          } else {
            logger.debug("Track's file length don't match cuesheet: {} or {}", flacFile, opusFile);
            return false;
          }
        } else {
          logger.debug("Track's files are missing: {} or {}", flacFile, opusFile);
          return false;
        }
      });
    });

    if (!accurateSplit) {
      logger.debug("Album's tracks are split incorrectly or missing: {}", album.getDir());
    }

    // check tags
    var accurateTags = tracks.stream().allMatch(track -> {
      var discNum = track.getTagValue("discnumber").map(Integer::parseInt).orElseThrow(() -> new IllegalArgumentException("No discnumber found for: " + track));
      var trackNum = track.getTagValue("tracknumber").map(Integer::parseInt).orElseThrow(() -> new IllegalArgumentException("No tracknumber found for: " + track));

      var discFound = album.getAudioToMetadata().values().stream()
          .flatMap(disc -> disc.getTagValue("discnumber").stream())
          .mapToInt(Integer::parseInt)
          .anyMatch(d -> d == discNum);

      if (!discFound) {
        logger.debug("Disc {} not present in source: {}", discNum, album.getDir());
        return true;
      }

      var flacFile = losslessDir.resolve(String.format("D%03dT%02d.flac", discNum, trackNum));
      var opusFile = lossyDir.resolve(String.format("D%03dT%02d.opus", discNum, trackNum));

      if (Files.exists(flacFile) && Files.exists(opusFile)) {
        var flacTags = metadata.parseMetadata(flacFile).getTags();
        var opusTags = metadata.parseMetadata(opusFile).getTags();

        if (track.getTags().equals(flacTags) && track.getTags().equals(opusTags)) {
          return true;
        } else {
          logger.debug("Track's file tags don't match expected metadata: {} or {}\nexpected={}\nflac    ={}\nopus    ={}", flacFile, opusFile, track.getTags(), flacTags, opusTags);
          return false;
        }
      } else {
        logger.debug("Track's files are missing: {} or {}", flacFile, opusFile);
        return false;
      }
    });

    if (!accurateTags) {
      logger.debug("Album's tracks are tagged incorrectly or missing: {}", album.getDir());
    }

    // TODO check for extra files

    return accurateSplit && accurateTags;
  }

  private String toPathSafe(String value) {
    return value.replaceAll("[<>:\"“‟‘‛”’″\\*\\?\\|/\\\\]+", "_");
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
        try (var files = Files.list(dir)) {
          files.forEach(file -> {
            try {
              Files.delete(file);
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          });
        }
      } else {
        Files.createDirectories(dir);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
