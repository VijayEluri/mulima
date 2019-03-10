package org.ajoberstar.mulima;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ajoberstar.mulima.init.SpringConfig;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.service.LibraryService;
import org.ajoberstar.mulima.ui.LibraryView;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.nio.file.Paths;
import java.util.List;

public final class Main extends Application {

  @Override
  public void start(Stage stage) {
    try (var context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
      stage.setTitle("Mulima");

      var libraryView = new LibraryView();
      var pane = libraryView.getRoot();

      var scene = new Scene(pane, 1920, 1080);
      stage.setScene(scene);
      stage.show();

      context.getBean(LibraryService.class).scan(Paths.get("D:", "originals", "flac-rips")).forEach((dir, metas) -> {
        metas.stream()
            .map(Metadata::denormalize)
            .flatMap(List::stream)
            .forEach(libraryView.getAllMetadata()::add);
      });
    }
//    try {
////      Pane pane = FXMLLoader.load(getClass().getResource("/org/ajoberstar/mulima/ui/metadata.fxml"));
//
//    } catch (IOException e) {
//      throw new UncheckedIOException(e);
//    }
  }

  public static void main(String[] args) {
//    launch(args);

    try (var context = new AnnotationConfigApplicationContext(SpringConfig.class)) {

      // [P1] Path -- directories to be scanned
      // [P2] List<Metadata> -- metadata to merge
      // [P3] List<Choice> -- failed merges
      // [P4] Metadata -- successful merges
      // [P5] Metadata -- ready to convert
      // [P6] Metadata -- "final" library


      // [P1] > [S1] > [P2] (success) -- parse metadata
      // [P2] > [S2] > [P4] (success) or [P3] (failure) -- merge metadata
      // [P3] > [S3] > [P4] (success) or [P3] (failure) -- user choice for merges
      // [P4] > [S4] > [P5] (if not up to date) or [P6] (if up to date) -- check if convert needed
      // [P5] > [S5] > [P6] -- convert


      // UI Stuff

      // Need a merge conflicts view
      // Need a MusicBrainz release chooser view
      // Need a progress bar view (but do I?)
    }
  }


}
