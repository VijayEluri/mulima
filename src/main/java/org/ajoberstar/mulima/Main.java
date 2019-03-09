package org.ajoberstar.mulima;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.ajoberstar.mulima.init.SpringConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class Main extends Application {
  @Override
  public void start(Stage stage) {
    var javaVersion = System.getProperty("java.version");
    var javafxVersion = System.getProperty("javafx.version");
    var label = new Label("Hellow, JavaFX " + javafxVersion + ", running on Java " + javaVersion);
    var scene = new Scene(new StackPane(label), 640, 480);
    stage.setScene(scene);
    stage.show();

    getHostServices().showDocument("https://musicbrainz.org/release/eea493e7-2ade-4538-80ed-c4e6d54ad3ac");
  }

  public static void main(String[] args) {
    try (var context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
      launch();
      // do stuff
    }
  }
}
