package org.ajoberstar.mulima.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.ajoberstar.mulima.meta.Metadata;

import java.util.List;

public class LibraryView {
  private final VBox root = new VBox();
  private final TableView<Metadata> table = new TableView<>();

  public LibraryView() {
    root.getChildren().add(table);

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
