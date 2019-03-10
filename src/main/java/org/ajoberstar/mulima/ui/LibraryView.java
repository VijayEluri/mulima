package org.ajoberstar.mulima.ui;

import java.nio.file.Path;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.ajoberstar.mulima.meta.Metadata;

public class LibraryView {
  private final VBox root = new VBox();
  private final TableView<Metadata> table = new TableView<>();

  public LibraryView() {
    root.getChildren().add(table);

    var source = new TableColumn<Metadata, Path>("source");
    source.setCellValueFactory(new PropertyValueFactory<>("sourceFile"));
    table.getColumns().add(source);

    var artwork = new TableColumn<Metadata, Path>("artwork");
    artwork.setCellValueFactory(new PropertyValueFactory<>("artworkFile"));
    table.getColumns().add(artwork);

    var audio = new TableColumn<Metadata, Path>("audio");
    audio.setCellValueFactory(new PropertyValueFactory<>("audioFile"));
    table.getColumns().add(audio);

    var columns = List.of("albumartist", "album", "discnumber", "tracknumber", "title", "artist");

    columns.forEach(columnName -> {
      var column = new TableColumn<Metadata, String>(columnName);
      column.setCellValueFactory(data -> {
        return new SimpleStringProperty(data.getValue().getTagValue(columnName).orElse(null));
      });
      table.getColumns().add(column);
    });
  }

  public Pane getRoot() {
    return root;
  }

  public ObservableList<Metadata> getAllMetadata() {
    return table.getItems();
  }
}
