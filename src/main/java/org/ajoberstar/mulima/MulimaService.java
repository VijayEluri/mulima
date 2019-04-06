package org.ajoberstar.mulima;

import java.util.List;
import java.util.Map;
import java.util.concurrent.SubmissionPublisher;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.ajoberstar.mulima.flow.Flows;
import org.ajoberstar.mulima.meta.Album;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.service.LibraryService;
import org.ajoberstar.mulima.service.MetadataService;
import org.ajoberstar.mulima.service.MusicBrainzService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MulimaService extends Service implements AutoCloseable {
  private static final Logger logger = LogManager.getLogger(MulimaService.class);

  private final SubmissionPublisher<Album> albumPublisher = Flows.publisher("album-publisher", 25);
  private final SubmissionPublisher<Album> invalidAlbumPublisher = Flows.publisher("invalid-album-publisher", 25);
  private final SubmissionPublisher<Map.Entry<Album, List<List<Metadata>>>> choicePublisher = Flows.publisher("choice-publisher", 25);
  private final SubmissionPublisher<Map.Entry<Album, List<Metadata>>> validAlbumPublisher = Flows.publisher("valid-album-publisher", 1000);
  private final SubmissionPublisher<Album> successfulConversionsPublisher = Flows.publisher("successful-conversion-publisher", 25);
  private final SubmissionPublisher<Map.Entry<Album, String>> failedConversionsPublisher = Flows.publisher("failed-conversion-publisher", 25);
  private final SubmissionPublisher<Map.Entry<String, Object>> progressPublisher = Flows.publisher("progress-publisher", 25);

  private final LibraryService library;
  private final MetadataService metadata;
  private final MusicBrainzService musicbrainz;

  public MulimaService(LibraryService library, MetadataService metadata, MusicBrainzService musicbrainz) {
    this.library = library;
    this.metadata = metadata;
    this.musicbrainz = musicbrainz;
  }

  public SubmissionPublisher<Album> getAlbumPublisher() {
    return albumPublisher;
  }

  public SubmissionPublisher<Album> getInvalidAlbumPublisher() {
    return invalidAlbumPublisher;
  }

  public SubmissionPublisher<Map.Entry<Album, List<List<Metadata>>>> getChoicePublisher() {
    return choicePublisher;
  }

  public SubmissionPublisher<Map.Entry<Album, List<Metadata>>> getValidAlbumPublisher() {
    return validAlbumPublisher;
  }

  public SubmissionPublisher<Album> getSuccessfulConversionsPublisher() {
    return successfulConversionsPublisher;
  }

  public SubmissionPublisher<Map.Entry<Album, String>> getFailedConversionsPublisher() {
    return failedConversionsPublisher;
  }

  public SubmissionPublisher<Map.Entry<String, Object>> getProgressPublisher() {
    return progressPublisher;
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
    // validator
    var albumSubscriber = Flows.<Album>subscriber("album-subscriber", 1, album -> {
      try {
        if (library.isPrepped(album)) {
          library.findMetadata(album).ifPresentOrElse(meta -> {
            validAlbumPublisher.submit(Map.entry(album, meta));
          }, () -> {
            // TODO should this go to invalid album instead?
            failedConversionsPublisher.submit(Map.entry(album, "Could not find metadata for album: " + album.getDir()));
          });
        } else {
          // TODO
          logger.warn("Invalid album: {}", album.getDir());
          // invalidAlbumPublisher.submit(album);
        }
      } catch (Exception e) {
        logger.error("Failed to determine if album is prepped: {}", album.getDir(), e);
      }
    });
    albumPublisher.subscribe(albumSubscriber);

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

    // converter
    var conversionSubscriber = Flows.<Map.Entry<Album, List<Metadata>>>subscriber("album-conversion-subscriber", Math.max(Runtime.getRuntime().availableProcessors() / 2, 1), entry -> {
      var album = entry.getKey();
      var metadata = entry.getValue();
      logger.info("Beginning conversion of {}", album.getDir());
      progressPublisher.submit(Map.entry("task", Map.entry(Thread.currentThread().getName(), album.getDir().toString())));
      try {
        library.convert(album, metadata, false);
        logger.info("Successfully converted {}", album.getDir());
        successfulConversionsPublisher.submit(album);
      } catch (Exception e) {
        logger.error("Failed to convert {}", album.getDir(), e);
        failedConversionsPublisher.submit(Map.entry(album, e.getMessage()));
      } finally {
        progressPublisher.submit(Map.entry("task", Map.entry(Thread.currentThread().getName(), null)));
      }
    });
    validAlbumPublisher.subscribe(conversionSubscriber);

    // success handler
    var successSubscriber = Flows.<Album>subscriber("successful-conversion-subscriber", 1, album -> {
      progressPublisher.submit(Map.entry("complete", 1));
    });
    successfulConversionsPublisher.subscribe(successSubscriber);

    // failure handler
    var failureSubscriber = Flows.<Map.Entry<Album, String>>subscriber("failed-conversion-subscriber", 1, entry -> {
      progressPublisher.submit(Map.entry("complete", 1));
    });
    failedConversionsPublisher.subscribe(failureSubscriber);

    // let's get this party started
    progressPublisher.submit(Map.entry("message", "Scanning for source albums."));
    var albums = library.getSourceAlbums();
    progressPublisher.submit(Map.entry("message", "Converting albums."));
    progressPublisher.submit(Map.entry("total", albums.size()));
    albums.forEach(albumPublisher::submit);
  }

  @Override
  public void close() {
    albumPublisher.close();
    invalidAlbumPublisher.close();
    choicePublisher.close();
    validAlbumPublisher.close();
    successfulConversionsPublisher.close();
    failedConversionsPublisher.close();
  }
}
