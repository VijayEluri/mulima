package org.ajoberstar.mulima.ui;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;
import org.ajoberstar.mulima.meta.CuePoint;
import org.ajoberstar.mulima.meta.CueSheetParser;
import org.ajoberstar.mulima.meta.Metadata;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;

public class MetadataController implements Initializable {
  @FXML private ListView<Metadata> list;

  @FXML private TextField sourceFile;
  @FXML private TextField artworkFile;
  @FXML private TextField audioFile;

  @FXML private TableView<CuePoint> cues;
  @FXML private TableColumn<CuePoint, Integer> index;
  @FXML private TableColumn<CuePoint, String> time;

  @FXML private TableView<Map<String, String>> tags;
  @FXML private TableColumn<Map<String, String>, String> tagName;
  @FXML private TableColumn<Map<String, String>, String> tagValue;

  @Override
  public void initialize(URL Location, ResourceBundle resources) {
    index.setCellValueFactory(new PropertyValueFactory<>("index"));
    time.setCellValueFactory(new PropertyValueFactory<>("time"));

    tagName.setCellValueFactory(new MapValueFactory("name"));
    tagValue.setCellValueFactory(new MapValueFactory("value"));

    var cellFactory = TextFieldListCell.forListView(new StringConverter<Metadata>() {
      @Override public String toString(Metadata object) {
        var meta = object.translate("generic");

        var artist = meta.getTagValue("albumartist").or(() -> meta.getTagValue("artist")).orElse("");
        var album = meta.getTagValue("album").orElse("");
        var disc = meta.getTagValue("discnumber")
            .map(Integer::parseInt)
            .map(v -> String.format("D%02d", v))
            .orElse("");
        var track = meta.getTagValue("tracknumber")
            .map(Integer::parseInt)
            .map(v -> String.format("T%02d", v))
            .orElse("");
        var title = meta.getTagValue("title").orElse("");
        return String.join(" ", artist, album, disc, track, title);
      }

      @Override public Metadata fromString(String string) {
        throw new UnsupportedOperationException("Cannot convert from String to Metadata.");
      }
    });

    list.setCellFactory(cellFactory);

    var cue = new CueSheetParser(ForkJoinPool.commonPool());
    cue.parse(Paths.get("D:", "test", "The Missing Piece.cue")).thenAccept(metadata -> {
      var dMeta = metadata.denormalize();
      list.getItems().setAll(dMeta);

      list.getFocusModel().focusedItemProperty().addListener((obs, oldMeta, newMeta) -> {
        sourceFile.setText(newMeta.getSourceFile().map(Path::toString).orElse(""));
        artworkFile.setText(newMeta.getArtworkFile().map(Path::toString).orElse(""));
        audioFile.setText(newMeta.getAudioFile().map(Path::toString).orElse(""));

        cues.getItems().setAll(newMeta.getCues());

        tags.getItems().clear();
        newMeta.getTags().entrySet().stream()
            .flatMap(entry -> entry.getValue().stream().map(value -> Map.of("name", entry.getKey(), "value", value)))
            .forEach(tags.getItems()::add);
      });
    });
  }

  private TreeItem<Metadata> toTreeItem(Metadata metadata) {
    var item = new TreeItem<>(metadata);
    metadata.getChildren().stream()
        .map(this::toTreeItem)
        .forEach(item.getChildren()::add);
    return item;
  }
}
