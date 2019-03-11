package org.ajoberstar.mulima;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.ajoberstar.mulima.init.SpringConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class GuiMain extends Application {
  private static final Logger logger = LogManager.getLogger(GuiMain.class);

  @Override
  public void start(Stage stage) {
    try (var context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
      stage.setTitle("Mulima");
//
      var pane = new VBox();
//
//      var scanLabel = new Label("Scan Progress");
//
//
//      pane.getChildren().add()
//
//      var libraryView = new LibraryView();
//      var pane = libraryView.getRoot();
//
      var scene = new Scene(pane, 1920, 1080);
      stage.setScene(scene);
      stage.show();

      // context.getBean(LibraryService.class).scan(Paths.get("D:", "originals",
      // "flac-rips")).forEach((dir, metas) -> {
      // metas.stream()
      // .map(Metadata::denormalize)
      // .flatMap(List::stream)
      // .forEach(libraryView.getAllMetadata()::add);
      // });
    }
    // try {
    //// Pane pane = FXMLLoader.load(getClass().getResource("/org/ajoberstar/mulima/ui/metadata.fxml"));
    //
    // } catch (IOException e) {
    // throw new UncheckedIOException(e);
    // }
  }

  public static void main(String[] args) {
    // launch(args);

    try (var context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
      logger.info("Mulima started.");
      launch(args);
    } catch (Exception e) {
      logger.error("Error occurred.", e);
    } finally {
      logger.info("Mulima complete.");
    }
  }
}
