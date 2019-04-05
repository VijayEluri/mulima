package org.ajoberstar.mulima;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SubmissionPublisher;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.ajoberstar.mulima.flow.Flows;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.service.LibraryService;
import org.ajoberstar.mulima.service.MetadataService;
import org.ajoberstar.mulima.service.MusicBrainzService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MulimaService extends Service implements AutoCloseable {
  private static final Logger logger = LogManager.getLogger(MulimaService.class);

  private final SubmissionPublisher<Path> sourceDirPublisher = Flows.publisher("source-dir-publisher", 25);
  private final SubmissionPublisher<Metadata> discoveredAlbumPublisher = Flows.publisher("discovered-album-publisher", 25);
  private final SubmissionPublisher<Metadata> invalidAlbumPublisher = Flows.publisher("invalid-album-publisher", 25);
  private final SubmissionPublisher<Map.Entry<Metadata, List<Metadata>>> choicePublisher = Flows.publisher("choice-publisher", 25);
  private final SubmissionPublisher<Map<String, Object>> decisionPublisher = Flows.publisher("decision-publisher", 25);
  private final SubmissionPublisher<Metadata> validAlbumPublisher = Flows.publisher("valid-album-publisher", 1000);
  private final SubmissionPublisher<Metadata> successfulConversionsPublisher = Flows.publisher("successful-conversion-publisher", 25);
  private final SubmissionPublisher<Metadata> failedConversionsPublisher = Flows.publisher("failed-conversion-publisher", 25);

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

  public SubmissionPublisher<Map<String, Object>> getDecisionPublisher() {
    return decisionPublisher;
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
    // // directory scanner
    // var sourceDirScannerSubscriber = Flows.<Path>subscriber("source-directory-scanner-subscriber", 1,
    // dir -> {
    // try {
    // var result = metadata.parseDir(dir);
    // if (!result.getChildren().isEmpty()) {
    // discoveredAlbumPublisher.submit(result);
    // }
    // } catch (Exception e) {
    // logger.error("Invalid metadata in dir: {}", dir, e);
    // }
    // });
    // sourceDirPublisher.subscribe(sourceDirScannerSubscriber);
    //
    // // validator
    // var validatorSubscriber = Flows.<Metadata>subscriber("metadata-validator-subscriber", 1, meta ->
    // {
    // var hasMusicBrainzData = meta.getCommonTagValue("musicbrainz_albumid").isPresent();
    // if (hasMusicBrainzData) {
    // validAlbumPublisher.submit(meta);
    // } else {
    // invalidAlbumPublisher.submit(meta);
    // }
    // });
    // discoveredAlbumPublisher.subscribe(validatorSubscriber);
    //
    // // musicbrainz lookup
    // var musicbrainzLookupSubscriber = Flows.<Metadata>subscriber("musicbrainz-lookup-subscriber", 1,
    // meta -> {
    // var possibleReleases = library.lookupChoices(meta);
    // if (possibleReleases.isEmpty()) {
    // logger.warn("No releases found for: {}", meta.getSourceFile());
    // } else if (possibleReleases.size() == 1) {
    // decisionPublisher.submit(Map.of("original", meta, "choice", possibleReleases.get(0),
    // "confidence", "probably"));
    // } else {
    // choicePublisher.submit(Map.entry(meta, possibleReleases));
    // }
    // });
    // invalidAlbumPublisher.subscribe(musicbrainzLookupSubscriber);
    //
    // var decisionSubscriber = Flows.<Map<String, Object>>subscriber("decision-subscriber", 1, decision
    // -> {
    // var meta = (Metadata) decision.get("original");
    // var choice = (Metadata) decision.get("choice");
    // var destYaml = meta.getSourceFile().resolve("metadata.yaml");
    // metadata.writeFile(choice, destYaml);
    // validAlbumPublisher.submit(choice);
    // });
    // decisionPublisher.subscribe(decisionSubscriber);
    //
    // // converter
    // var conversionSubscriber = Flows.<Metadata>subscriber("album-conversion-subscriber",
    // Math.max(Runtime.getRuntime().availableProcessors() / 2, 1), meta -> {
    // logger.info("Starting conversion of: {}", meta.getSourceFile());
    // try {
    // library.convert(meta, losslessDir, lossyDir);
    // logger.info("Successfully converted: {}", meta.getSourceFile());
    // // successfulConversionsPublisher.submit(meta);
    // } catch (Exception e) {
    // logger.error("Failed to convert: {}", meta.getSourceFile(), e);
    // // failedConversionsPublisher.submit(meta);
    // }
    // });
    // validAlbumPublisher.subscribe(conversionSubscriber);
    //
    // // success logger
    // var successSubscriber = Flows.<Metadata>subscriber("successful-conversion-subscriber", 1, meta ->
    // {
    // logger.info("Successfully converted: {}", meta.getSourceFile());
    // });
    // successfulConversionsPublisher.subscribe(successSubscriber);
    //
    // // failure logger
    // var failureSubscriber = Flows.<Metadata>subscriber("failed-conversion-subscriber", 1, meta -> {
    // logger.error("Failed to convert: {}", meta.getSourceFile());
    // });
    // failedConversionsPublisher.subscribe(failureSubscriber);
    //
    // // lets get this party started
    // try (var fileStream = Files.walk(sourceDir)) {
    // fileStream
    // .filter(Files::isDirectory)
    // .forEach(sourceDirPublisher::submit);
    // } catch (IOException e) {
    // throw new UncheckedIOException(e);
    // }
  }

  @Override
  public void close() {
    sourceDirPublisher.close();
    discoveredAlbumPublisher.close();
    invalidAlbumPublisher.close();
    choicePublisher.close();
    validAlbumPublisher.close();
    successfulConversionsPublisher.close();
    failedConversionsPublisher.close();
  }
}
