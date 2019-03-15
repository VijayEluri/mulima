package org.ajoberstar.mulima;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.ajoberstar.mulima.flow.Flows;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.service.LibraryService;
import org.ajoberstar.mulima.service.MetadataService;
import org.ajoberstar.mulima.service.MusicBrainzService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

public class MulimaService extends Service implements AutoCloseable {
  private static final Logger logger = LogManager.getLogger(MulimaService.class);

  private final SubmissionPublisher<Map.Entry<String, Object>> toUIPublisher = Flows.publisher("to-ui-publisher", 100);
  private final SubmissionPublisher<Map.Entry<String, Object>> toBackendPublisher = Flows.publisher("to-backend-publisher", 100);
  private final SubmissionPublisher<Path> sourceDirPublisher = Flows.publisher("source-dir-publisher", 25);
  private final SubmissionPublisher<Metadata> discoveredAlbumPublisher = Flows.publisher("discovered-album-publisher", 25);
  private final SubmissionPublisher<Metadata>  invalidAlbumPublisher = Flows.<Metadata>publisher("invalid-album-publisher", 25);
  private final SubmissionPublisher<Map.Entry<Metadata, List<Metadata>>>  choicePublisher = Flows.publisher("choice-publisher", 25);
  private final SubmissionPublisher<Metadata>  validAlbumPublisher = Flows.<Metadata>publisher("valid-album-publisher", 25);
  private final SubmissionPublisher<Metadata>  successfulConversionsPublisher = Flows.<Metadata>publisher("successful-conversion-publisher", 25);
  private final SubmissionPublisher<Metadata>  failedConversionsPublisher = Flows.<Metadata>publisher("failed-conversion-publisher", 25);

  private final LibraryService library;
  private final MetadataService metadata;
  private final MusicBrainzService musicbrainz;
  private final Path sourceDir;
  private final Path losslessDir;
  private final Path lossyDir;

  public MulimaService(LibraryService library, MetadataService metadata, MusicBrainzService musicbrainz, Path sourceDir, Path losslessDir, Path lossyDir) {
    this.library = library;
    this.metadata = metadata;
    this.musicbrainz = musicbrainz;
    this.sourceDir = sourceDir;
    this.losslessDir = losslessDir;
    this.lossyDir = lossyDir;
  }

  public SubmissionPublisher<Map.Entry<String, Object>> getToUIPublisher() {
    return toUIPublisher;
  }

  public SubmissionPublisher<Map.Entry<String, Object>> getToBackendPublisher() {
    return toBackendPublisher;
  }

  public SubmissionPublisher<Path> getSourceDirPublisher() {
    return sourceDirPublisher;
  }

  public SubmissionPublisher<Metadata> getDiscoveredAlbumPublisher() {
    return discoveredAlbumPublisher;
  }

  public SubmissionPublisher<Metadata> getInvalidAlbumPublisher() {
    return invalidAlbumPublisher;
  }

  public SubmissionPublisher<Map.Entry<Metadata, List<Metadata>>> getChoicePublisher() {
    return choicePublisher;
  }

  public SubmissionPublisher<Metadata> getValidAlbumPublisher() {
    return validAlbumPublisher;
  }

  public SubmissionPublisher<Metadata> getSuccessfulConversionsPublisher() {
    return successfulConversionsPublisher;
  }

  public SubmissionPublisher<Metadata> getFailedConversionsPublisher() {
    return failedConversionsPublisher;
  }

  @Override
  public Task<Void> createTask() {
    return new Task<>() {

      @Override
      public Void call() {
        process();
        return null;
      }
    };
  }

  private void process() {
    var fromUISubscriber = Flows.<Map.Entry<String, Object>>subscriber("ui-command-subscriber", 1, item -> {
      logger.error("Received message from UI: {} -> {}", item.getKey(), item.getValue());
    });
    toBackendPublisher.subscribe(fromUISubscriber);

    // directory scanner
    var sourceDirScannerSubscriber = Flows.<Path>subscriber("source-directory-scanner-subscriber",1, dir -> {
      var result = metadata.parseDir(dir);
      if (!result.getChildren().isEmpty()) {
        discoveredAlbumPublisher.submit(result);
      }
    });
    sourceDirPublisher.subscribe(sourceDirScannerSubscriber);

    // validator
    var validatorSubscriber = Flows.<Metadata>subscriber("metadata-validator-subscriber", 1, meta -> {
      var hasMusicBrainzData = meta.getChildren().stream()
          .map(m -> meta.getTagValue("musicbrainz_albumid"))
          .allMatch(Optional::isPresent);

      if (hasMusicBrainzData) {
        //          validAlbumPublisher.submit(meta);
      } else {
        invalidAlbumPublisher.submit(meta);
      }
    });
    discoveredAlbumPublisher.subscribe(validatorSubscriber);

    // musicbrainz lookup
    var musicbrainzLookupSubscriber = Flows.<Metadata>subscriber("musicbrainz-lookup-subscriber", 1, meta -> {
      var audioToTracks = meta.getChildren().stream()
          .collect(Collectors.groupingBy(m -> m.getAudioFile().get()));

      var possibleReleases = audioToTracks.entrySet().stream()
          .map(entry -> musicbrainz.calculateDiscId(entry.getValue(), entry.getKey()))
          .flatMap(discId -> musicbrainz.lookupByDiscId(discId).stream())
          .collect(Collectors.toList());

      if (possibleReleases.isEmpty()) {
        logger.warn("No releases found for: {}", meta.getSourceFile());
      } else {
        choicePublisher.submit(Map.entry(meta, possibleReleases));
      }
    });
    invalidAlbumPublisher.subscribe(musicbrainzLookupSubscriber);

    // musicbrainz chooser
    var releaseChoiceSubscriber = Flows.<Map.Entry<Metadata, List<Metadata>>>subscriber("musicbrainz-release-chooser-subscriber", 25, choice -> {
      var meta = choice.getKey();
      var candidates = choice.getValue();
      var artist = meta.getChildren().get(0).getTagValue("albumartist").or(() -> meta.getChildren().get(0).getTagValue("artist")).orElse("Unknown artist");
      var album = meta.getChildren().get(0).getTagValue("album").orElse("Unknown album");
      System.out.println(String.format("Choice for: %s - %s (%s)", artist, album, meta.getSourceFile()));
      candidates.forEach(candidate -> {
        var cReleaseId = candidate.getTagValue("musicbrainz_albumid").orElse("Unknown release ID");
        var cArtist = candidate.getTagValue("albumartist").or(() -> candidate.getTagValue("artist")).orElse("Unknown artist");
        var cAlbum = candidate.getTagValue("album").orElse("Unkown album");
        System.out.println(String.format("  * %s - %s (%s)", cArtist, cAlbum, cReleaseId));
      });
      //        toUIPublisher.submit(Map.entry("Choice", choice));
    });
    choicePublisher.subscribe(releaseChoiceSubscriber);

    // converter
    var conversionSubscriber = Flows.<Metadata>subscriber("album-conversion-subscriber", Math.max(Runtime.getRuntime().availableProcessors() / 2, 1), meta -> {
      logger.info("Starting conversion of: {}", meta.getSourceFile());
      try {
        library.convert(meta, losslessDir, lossyDir);
        successfulConversionsPublisher.submit(meta);
      } catch (Exception e) {
        failedConversionsPublisher.submit(meta);
      }
    });
    //      validAlbumPublisher.subscribe(conversionSubscriber);

    // success logger
    var successSubscriber = Flows.<Metadata>subscriber("successful-conversion-subscriber", 1, meta -> {
      logger.info("Successfully converted: {}", meta.getSourceFile());
    });
    //      successfulConversionsPublisher.subscribe(successSubscriber);

    // failure logger
    var failureSubscriber = Flows.<Metadata>subscriber("failed-conversion-subscriber", 1, meta -> {
      logger.error("Failed to convert: {}", meta.getSourceFile());
    });
    //      failedConversionsPublisher.subscribe(failureSubscriber);

    // lets get this party started
    try (var fileStream = Files.walk(sourceDir)) {
      fileStream
          .filter(Files::isDirectory)
          .forEach(sourceDirPublisher::submit);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() {
    toUIPublisher.close();
    toBackendPublisher.close();
    sourceDirPublisher.close();
    discoveredAlbumPublisher.close();
    invalidAlbumPublisher.close();
    choicePublisher.close();
    validAlbumPublisher.close();
    successfulConversionsPublisher.close();
    failedConversionsPublisher.close();
  }
}
