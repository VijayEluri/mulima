package org.ajoberstar.mulima;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import org.ajoberstar.mulima.flow.Flows;
import org.ajoberstar.mulima.init.SpringConfig;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.service.LibraryService;
import org.ajoberstar.mulima.service.MetadataService;
import org.ajoberstar.mulima.service.MusicBrainzService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public final class Main {
  private static final Logger logger = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    try (var context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
      // Metrics
      Metrics.addRegistry(context.getBean(MeterRegistry.class));
      Metrics.globalRegistry.config().commonTags(
        "application", "mulima",
        "execution", UUID.randomUUID().toString()
      );
      new ClassLoaderMetrics().bindTo(Metrics.globalRegistry);
      new JvmMemoryMetrics().bindTo(Metrics.globalRegistry);
      new JvmGcMetrics().bindTo(Metrics.globalRegistry);
      new ProcessorMetrics().bindTo(Metrics.globalRegistry);
      new JvmThreadMetrics().bindTo(Metrics.globalRegistry);

      ExecutorServiceMetrics.monitor(Metrics.globalRegistry, ForkJoinPool.commonPool(), "fork-join-common-pool");

      // Now it begins
      logger.info("Mulima started.");
      var library = context.getBean(LibraryService.class);
      var metadata = context.getBean(MetadataService.class);
      var musicbrainz = context.getBean(MusicBrainzService.class);

      // directories to be scanned
      var sourceDirPublisher = Flows.<Path>publisher();

      // discovered source metadata
      var discoveredAlbumPublisher = Flows.<Metadata>publisher();

      // invalid metadata
      var invalidAlbumPublisher = Flows.<Metadata>publisher();

      // musicbrainz chooser
      var choicePublisher = Flows.<Map.Entry<Metadata, List<Metadata>>>publisher();

      // validated metadata
      var validAlbumPublisher = Flows.<Metadata>publisher();

      // converted metadata
      var successfulConversionsPublisher = Flows.<Metadata>publisher();
      var failedConversionsPublisher = Flows.<Metadata>publisher();

      var blocking = context.getBean("blocking", ExecutorService.class);

      // directory scanner
      var sourceDirScannerSubscriber = Flows.<Path>subscriber("Source directory scanner", blocking, 1, dir -> {
        var result = metadata.parseDir(dir);
        if (!result.getChildren().isEmpty()) {
          discoveredAlbumPublisher.submit(result);
        }
      });
      sourceDirPublisher.subscribe(sourceDirScannerSubscriber);

      // validator
      var validatorSubscriber = Flows.<Metadata>subscriber("Metadata validator", blocking, 1, meta -> {
        var hasMusicBrainzData = meta.getChildren().stream()
            .map(m -> meta.getTagValue("musicbrainz_albumid"))
            .allMatch(Optional::isPresent);

        if (hasMusicBrainzData) {
          validAlbumPublisher.submit(meta);
        } else {
          invalidAlbumPublisher.submit(meta);
        }
      });
      discoveredAlbumPublisher.subscribe(validatorSubscriber);

      // musicbrainz lookup
      var musicbrainzLookupSubscriber = Flows.<Metadata>subscriber("MusicBrainz lookup", blocking, Runtime.getRuntime().availableProcessors(), meta -> {
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
      var releaseChoiceSubscriber = Flows.<Map.Entry<Metadata, List<Metadata>>>subscriber("MusicBrainz release chooser", blocking, 1, choice -> {
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
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
      choicePublisher.subscribe(releaseChoiceSubscriber);

      // converter
      var conversionSubscriber = Flows.<Metadata>subscriber("Album conversion", blocking, Math.max(Runtime.getRuntime().availableProcessors() / 2, 1), meta -> {
        logger.info("Starting conversion of: {}", meta.getSourceFile());
        var losslessDir = Paths.get("D:", "test", "lossless");
        var lossyDir = Paths.get("D:", "test", "lossy");
        try {
          library.convert(meta, losslessDir, lossyDir);
          successfulConversionsPublisher.submit(meta);
        } catch (Exception e) {
          failedConversionsPublisher.submit(meta);
        }
      });
      validAlbumPublisher.subscribe(conversionSubscriber);

      // success logger
      var successSubscriber = Flows.<Metadata>subscriber("Successful conversion", ForkJoinPool.commonPool(), 1, meta -> {
        logger.info("Successfully converted: {}", meta.getSourceFile());
      });
      successfulConversionsPublisher.subscribe(successSubscriber);

      // failure logger
      var failureSubscriber = Flows.<Metadata>subscriber("Failed conversion", ForkJoinPool.commonPool(), 1, meta -> {
        logger.error("Failed to convert: {}", meta.getSourceFile());
      });
      failedConversionsPublisher.subscribe(failureSubscriber);

      // lets get this party started
      try (var fileStream = Files.walk(Paths.get("D:", "originals", "flac-rips"))) {
        fileStream
            .filter(Files::isDirectory)
            .forEach(sourceDirPublisher::submit);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } catch (Exception e) {
      logger.error("Error occurred.", e);
    } finally {
      logger.info("Mulima complete.");
    }
  }
}
