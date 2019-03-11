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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

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

      // directories to be scanned
      var sourceDirPublisher = Flows.<Path>publisher();

      // discovered source metadata
      var discoveredAlbumPublisher = Flows.<Metadata>publisher();

      // invalid metadata
      var invalidAlbumPublisher = Flows.<Metadata>publisher();

      // validated metadata
      var validAlbumPublisher = Flows.<Metadata>publisher();

      // converted metadata
      var successfulConversionsPublisher = Flows.<Metadata>publisher();

      var blocking = context.getBean("blocking", ExecutorService.class);

      // directory scanner
      var sourceDirScannerSubscriber = Flows.<Path>subscriber("Source directory scanner", blocking, Math.max(Runtime.getRuntime().availableProcessors() / 2, 1), dir -> {
        var result = metadata.parseDir(dir);
        if (!result.getChildren().isEmpty()) {
          discoveredAlbumPublisher.submit(result);
        }
      });
      sourceDirPublisher.subscribe(sourceDirScannerSubscriber);

      // validator
      var validatorSubscriber = Flows.<Metadata>subscriber("Metadata validator", blocking, 25, meta -> {
        // TODO actually validate
        validAlbumPublisher.submit(meta);
      });
      discoveredAlbumPublisher.subscribe(validatorSubscriber);

      // converter
      var conversionSubscriber = Flows.<Metadata>subscriber("Album conversion", blocking, Math.max(Runtime.getRuntime().availableProcessors() / 2, 1), meta -> {
        logger.info("Starting conversion of: {}", meta.getSourceFile());
        var losslessDir = Paths.get("D:", "test", "lossless");
        var lossyDir = Paths.get("D:", "test", "lossy");
        library.convert(meta, losslessDir, lossyDir);
        successfulConversionsPublisher.submit(meta);
      });
      validAlbumPublisher.subscribe(conversionSubscriber);

      // success logger
      var successSubscriber = Flows.<Metadata>subscriber("Successful conversion", ForkJoinPool.commonPool(), 25, meta -> {
        logger.info("Successfully converted: {}", meta.getSourceFile());
      });
      successfulConversionsPublisher.subscribe(successSubscriber);

      // [P3] List<Choice> -- failed merges
      // [P4] Metadata -- successful merges
      // [P5] Metadata -- ready to convert
      // [P6] Metadata -- "final" library


      // [P1] > [S1] > [P2] (success) -- parse metadata
      // [P2] > [S2] > [P4] (success) or [P3] (failure) -- merge metadata
      // [P3] > [S3] > [P4] (success) or [P3] (failure) -- user choice for merges
      // [P4] > [S4] > [P5] (if not up to date) or [P6] (if up to date) -- check if convert needed
      // [P5] > [S5] > [P6] -- convert

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
