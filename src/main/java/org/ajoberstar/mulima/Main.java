package org.ajoberstar.mulima;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.ajoberstar.mulima.flow.Flows;
import org.ajoberstar.mulima.init.SpringConfig;
import org.ajoberstar.mulima.meta.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public final class Main extends Application {
  private static final Logger logger = LogManager.getLogger(Main.class);

  private ConfigurableApplicationContext context;
  private MulimaService mulima;

  @Override
  public void init() {
    context = new AnnotationConfigApplicationContext(SpringConfig.class);
    mulima = context.getBean(MulimaService.class);
    mulima.start();
  }

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

    var fromBackendSubscriber = Flows.<Map.Entry<String, Object>>subscriber("backend-response-subscriber", 1, item -> {
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
    mulima.getToUIPublisher().subscribe(fromBackendSubscriber);

    definitely.setOnAction(event -> {
      var choice = table.getSelectionModel().getSelectedItem();
      if (choice == null) {
        return;
      }

      mulima.getToBackendPublisher().submit(Map.entry("Choice", choice));

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
  }

  @Override
  public void stop() {
    context.stop();
  }

  public static void main(String[] args) {
    try {
      logger.info("Mulima started.");
      launch(args);
      logger.info("Mulima completed.");
    } catch (Exception e) {
      logger.error("Mulima failed.", e);
    }
  }
}
