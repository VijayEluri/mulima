package org.ajoberstar.mulima;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.ajoberstar.mulima.flow.Flows;
import org.ajoberstar.mulima.init.SpringConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class Main extends Application {
  private static final Logger logger = LogManager.getLogger(Main.class);

  private ConfigurableApplicationContext context;
  private MulimaService mulima;

  @Override
  public void init() {
    context = new AnnotationConfigApplicationContext(SpringConfig.class);
    mulima = context.getBean(MulimaService.class);
  }

  @Override
  public void start(Stage stage) {
    var totalAlbums = new AtomicInteger(1);
    var completeAlbums = new AtomicInteger(0);
    var failedAlbums = new AtomicInteger(0);

    var pane = new VBox();

    var progress = new ProgressBar();
    progress.setPrefWidth(550);
    pane.getChildren().add(progress);

    var tasks = new TableView<Map>();
    tasks.setPrefHeight(200);
    var threadTask = new TableColumn<Map, String>("Task");
    threadTask.setCellValueFactory(new MapValueFactory<>("task"));
    threadTask.setPrefWidth(550);
    tasks.getColumns().addAll(threadTask);
    pane.getChildren().add(tasks);

    var messages = new ListView<String>();
    messages.setPrefHeight(500);
    pane.getChildren().add(messages);

    var progressSubscriber = Flows.<Map.Entry<String, Object>>subscriber("progress-subscriber", 1, event -> {
      var kind = event.getKey();
      if ("total".equals(kind)) {
        totalAlbums.set((int) event.getValue());
        Platform.runLater(() -> {
          progress.setProgress(0);
        });
      } else if ("complete".equals(kind)) {
        var percent = completeAlbums.incrementAndGet() / (double) totalAlbums.get();
        Platform.runLater(() -> {
          progress.setProgress(percent);
        });
      } else if ("task".equals(kind)) {
        var map = (Map.Entry<String, String>) event.getValue();
        var thread = map.getKey();
        var task = map.getValue();
        Platform.runLater(() -> {
          tasks.getItems().removeIf(t -> thread.equals(t.get("name")));
          if (!task.isBlank()) {
            tasks.getItems().add(Map.of("name", thread, "task", task));
          }
        });
      } else if ("message".equals(kind)) {
        Platform.runLater(() -> {
          messages.getItems().add(event.getValue().toString());
        });
      }
    });
    mulima.getProgressPublisher().subscribe(progressSubscriber);

    stage.setTitle("Mulima");

    var scene = new Scene(pane, 550, 800);
    stage.setScene(scene);
    stage.show();

    mulima.start();
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
