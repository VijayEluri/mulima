package org.ajoberstar.mulima;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

public final class Main extends Application {
  private static final Logger logger = LogManager.getLogger(Main.class);

  private static final SubmissionPublisher<Map.Entry<String, Object>> toUIPublisher = Flows.publisher(Executors.newSingleThreadExecutor(), 100);
  private static final SubmissionPublisher<Map.Entry<String, Object>> toBackendPublisher = Flows.publisher(Executors.newSingleThreadExecutor(), 100);

  @Override
  public void start(Stage stage) {
    var pane = new VBox();

    stage.setTitle("Mulima: Waiting for next choice");

    var table = new TableView<Metadata>();
    var artistCol = new TableColumn<Metadata, String>("Artist");
    var albumCol = new TableColumn<Metadata, String>("Album");
    var dateCol = new TableColumn<Metadata, String>("Release Date");
    var labelCol = new TableColumn<Metadata, String>("Label");
    var catalogNumberCol = new TableColumn<Metadata, String>("Catalog Number");
    var barcodeCol = new TableColumn<Metadata, String>("Barcode");
    var releaseIdCol = new TableColumn<Metadata, String>("Release ID");

    artistCol.setCellValueFactory(cdf -> {
      var meta = cdf.getValue();
      var value = meta.getTagValue("albumartist").or(() -> meta.getTagValue("artist")).orElse("Unknown");
      return new ReadOnlyStringWrapper(value);
    });

    albumCol.setCellValueFactory(cdf -> {
      var meta = cdf.getValue();
      var value = meta.getTagValue("album").orElse("Unknown");
      return new ReadOnlyStringWrapper(value);
    });

    dateCol.setCellValueFactory(cdf -> {
      var meta = cdf.getValue();
      var value = meta.getTagValue("date").orElse("Unknown");
      return new ReadOnlyStringWrapper(value);
    });

    labelCol.setCellValueFactory(cdf -> {
      var meta = cdf.getValue();
      var value = meta.getTagValue("label").orElse("Unknown");
      return new ReadOnlyStringWrapper(value);
    });

    catalogNumberCol.setCellValueFactory(cdf -> {
      var meta = cdf.getValue();
      var value = meta.getTagValue("catalogNumber").orElse("Unknown");
      return new ReadOnlyStringWrapper(value);
    });

    barcodeCol.setCellValueFactory(cdf -> {
      var meta = cdf.getValue();
      var value = meta.getTagValue("barcode").orElse("Unknown");
      return new ReadOnlyStringWrapper(value);
    });

    releaseIdCol.setCellValueFactory(cdf -> {
      var meta = cdf.getValue();
      var value = meta.getTagValue("musicbrainz_albumid").orElse("Unknown");
      return new ReadOnlyStringWrapper(value);
    });

    table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    table.getColumns().addAll(artistCol, albumCol, dateCol, labelCol, catalogNumberCol, barcodeCol, releaseIdCol);
    pane.getChildren().add(table);

    var buttons = new ButtonBar();
    var definitely = new Button("Definitely");
    var probably = new Button("Probably");
    var none = new Button("None");
    var skip = new Button("Skip");
    buttons.getButtons().addAll(definitely, probably, none, skip);
    pane.getChildren().add(buttons);

    var web = new WebView();
    pane.getChildren().add(web);

    table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Metadata>) change -> {
      change.getList().forEach(metadata -> {
        var releaseId = metadata.getTagValue("musicbrainz_albumid").get();
        var uri = String.format("https://musicbrainz.org/release/%s", releaseId);
        web.getEngine().load(uri);
      });
    });

    var progress = new ProgressBar();
    progress.setMaxWidth(1920);
    progress.progressProperty().bind(web.getEngine().getLoadWorker().progressProperty());
    pane.getChildren().add(progress);


    var choiceBarrier = new CyclicBarrier(2);

    var fromBackendSubscriber = Flows.<Map.Entry<String, Object>>subscriber("Backend responses", Executors.newSingleThreadExecutor(), 1, item -> {
      logger.error("Received message from backend: {} -> {}", item.getKey(), item.getValue());
      if ("Choice".equals(item.getKey())) {
        var entry = (Map.Entry<Metadata, List<Metadata>>) item.getValue();
        var meta = entry.getKey();
        var options = entry.getValue();

        var artist = meta.getTagValue("albumartist").or(() -> meta.getTagValue("artist")).orElse("Unknown");
        var album = meta.getTagValue("album").orElse("Unknown");
        var source = meta.getSourceFile();

        Platform.runLater(() -> {
          stage.setTitle(String.format("Mulima: %s - %s (%s)", artist, album, source));
          table.getItems().setAll(options);
        });

        try {
          choiceBarrier.await();
          choiceBarrier.reset();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (BrokenBarrierException e) {
          // TODO what?
        }
      } else {
        logger.warn("Unknown message from backend: {} -> {}", item.getKey(), item.getValue());
      }
    });
    toUIPublisher.subscribe(fromBackendSubscriber);

    definitely.setOnAction(event -> {
      var choice = table.getSelectionModel().getSelectedItem();
      if (choice == null) {
        return;
      }

      toBackendPublisher.submit(Map.entry("Choice", choice));

      try {
        choiceBarrier.await();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (BrokenBarrierException e) {
        // TODO what?
      }
      stage.setTitle("Mulima: Waiting for next choice");
      table.getItems().clear();
    });

    var scene = new Scene(pane, 1024, 768);
    stage.setScene(scene);
    stage.show();

    var backgroundService = new Service() {
      @Override
      public Task<Void> createTask() {
        return new Task<>() {
          @Override
          protected Void call() {
            runBackend();
            return null;
          }
        };
      }
    };
    backgroundService.start();
  }

  public static void runBackend() {
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

      var fromUISubscriber = Flows.<Map.Entry<String, Object>>subscriber("UI commands", Executors.newSingleThreadExecutor(), 1, item -> {
        logger.error("Received message from UI: {} -> {}", item.getKey(), item.getValue());
      });
      toBackendPublisher.subscribe(fromUISubscriber);

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
      var validatorSubscriber = Flows.<Metadata>subscriber("Metadata validator", blocking, Runtime.getRuntime().availableProcessors(), meta -> {
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
      var musicbrainzLookupSubscriber = Flows.<Metadata>subscriber("MusicBrainz lookup", blocking, 1, meta -> {
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
        toUIPublisher.submit(Map.entry("Choice", choice));
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

  public static void main(String[] args) {
    launch(args);
  }
}
