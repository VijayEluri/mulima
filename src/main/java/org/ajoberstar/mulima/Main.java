package org.ajoberstar.mulima;

import javafx.application.Application;
import javafx.stage.Stage;

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
    // stage.setTitle("Mulima: Waiting for next choice");
    //
    // var currentMeta = new AtomicReference<Metadata>();
    //
    // var pane = new VBox();
    //
    // var table = new TableView<Metadata>();
    // var artistCol = new TableColumn<Metadata, String>("Artist");
    // artistCol.setPrefWidth(200);
    // var albumCol = new TableColumn<Metadata, String>("Album");
    // albumCol.setPrefWidth(200);
    // var dateCol = new TableColumn<Metadata, String>("Release Date");
    // dateCol.setPrefWidth(75);
    // var labelCol = new TableColumn<Metadata, String>("Label");
    // labelCol.setPrefWidth(100);
    // var catalogNumberCol = new TableColumn<Metadata, String>("Catalog Number");
    // catalogNumberCol.setPrefWidth(75);
    // var barcodeCol = new TableColumn<Metadata, String>("Barcode");
    // barcodeCol.setPrefWidth(75);
    // var releaseIdCol = new TableColumn<Metadata, String>("Release ID");
    // releaseIdCol.setPrefWidth(200);
    //
    // artistCol.setCellValueFactory(cdf -> {
    // var meta = cdf.getValue();
    // var value = meta.getCommonTagValue("albumartist").or(() ->
    // meta.getCommonTagValue("artist")).orElse("Unknown");
    // return new ReadOnlyStringWrapper(value);
    // });
    //
    // albumCol.setCellValueFactory(cdf -> {
    // var meta = cdf.getValue();
    // var value = meta.getCommonTagValue("album").orElse("Unknown");
    // return new ReadOnlyStringWrapper(value);
    // });
    //
    // dateCol.setCellValueFactory(cdf -> {
    // var meta = cdf.getValue();
    // var value = meta.getCommonTagValue("date").orElse("Unknown");
    // return new ReadOnlyStringWrapper(value);
    // });
    //
    // labelCol.setCellValueFactory(cdf -> {
    // var meta = cdf.getValue();
    // var value = meta.getCommonTagValue("label").orElse("Unknown");
    // return new ReadOnlyStringWrapper(value);
    // });
    //
    // catalogNumberCol.setCellValueFactory(cdf -> {
    // var meta = cdf.getValue();
    // var value = meta.getCommonTagValue("catalogNumber").orElse("Unknown");
    // return new ReadOnlyStringWrapper(value);
    // });
    //
    // barcodeCol.setCellValueFactory(cdf -> {
    // var meta = cdf.getValue();
    // var value = meta.getCommonTagValue("barcode").orElse("Unknown");
    // return new ReadOnlyStringWrapper(value);
    // });
    //
    // releaseIdCol.setCellValueFactory(cdf -> {
    // var meta = cdf.getValue();
    // var value = meta.getCommonTagValue("musicbrainz_albumid").orElse("Unknown");
    // return new ReadOnlyStringWrapper(value);
    // });
    //
    // table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    // table.getColumns().addAll(artistCol, albumCol, dateCol, labelCol, catalogNumberCol, barcodeCol,
    // releaseIdCol);
    // pane.getChildren().add(table);
    //
    // var buttons = new ButtonBar();
    // var definitely = new Button("Definitely");
    // var probably = new Button("Probably");
    // var none = new Button("None");
    // var skip = new Button("Skip");
    // buttons.getButtons().addAll(definitely, probably, none, skip);
    // pane.getChildren().add(buttons);
    //
    // var web = new WebView();
    // pane.getChildren().add(web);
    //
    // // Actions/Events
    //
    // table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Metadata>) change ->
    // {
    // change.getList().forEach(metadata -> {
    // var releaseId = metadata.getCommonTagValue("musicbrainz_albumid").get();
    // var uri = String.format("https://musicbrainz.org/release/%s", releaseId);
    // web.getEngine().load(uri);
    // });
    // });
    //
    // var progress = new ProgressBar();
    // progress.setMaxWidth(960);
    // progress.progressProperty().bind(web.getEngine().getLoadWorker().progressProperty());
    // pane.getChildren().add(progress);
    //
    // var choiceBarrier = new CyclicBarrier(2);
    //
    // var choiceSubscriber = Flows.<Map.Entry<Metadata,
    // List<Metadata>>>subscriber("backend-response-subscriber", 1, entry -> {
    // var meta = entry.getKey();
    // var options = entry.getValue();
    //
    // var artist = meta.getCommonTagValue("albumartist").or(() ->
    // meta.getCommonTagValue("artist")).orElse("Unknown");
    // var album = meta.getCommonTagValue("album").orElse("Unknown");
    // var source = meta.getSourceFile();
    //
    // currentMeta.set(meta);
    //
    // Platform.runLater(() -> {
    // stage.setTitle(String.format("Mulima: %s - %s (%s)", artist, album, source));
    // table.getItems().setAll(options);
    // });
    //
    // try {
    // choiceBarrier.await();
    // choiceBarrier.reset();
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // } catch (BrokenBarrierException e) {
    // // TODO what?
    // }
    // });
    // mulima.getChoicePublisher().subscribe(choiceSubscriber);
    //
    // definitely.setOnAction(event -> {
    // var choice = table.getSelectionModel().getSelectedItem();
    // if (choice == null) {
    // return;
    // }
    //
    // var meta = currentMeta.getAndSet(null);
    // var decision = Map.of("original", meta, "choice", choice, "confidence", "definitely");
    //
    // mulima.getDecisionPublisher().submit(decision);
    //
    // try {
    // choiceBarrier.await();
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // } catch (BrokenBarrierException e) {
    // // TODO what?
    // }
    // stage.setTitle("Mulima: Waiting for next choice");
    // table.getItems().clear();
    // });
    //
    // probably.setOnAction(event -> {
    // var choice = table.getSelectionModel().getSelectedItem();
    // if (choice == null) {
    // return;
    // }
    //
    // var meta = currentMeta.getAndSet(null);
    // var decision = Map.of("original", meta, "choice", choice, "confidence", "probably");
    //
    // mulima.getDecisionPublisher().submit(decision);
    //
    // try {
    // choiceBarrier.await();
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // } catch (BrokenBarrierException e) {
    // // TODO what?
    // }
    // stage.setTitle("Mulima: Waiting for next choice");
    // table.getItems().clear();
    // });
    //
    // none.setOnAction(event -> {
    // currentMeta.set(null);
    // try {
    // choiceBarrier.await();
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // } catch (BrokenBarrierException e) {
    // // TODO what?
    // }
    // stage.setTitle("Mulima: Waiting for next choice");
    // table.getItems().clear();
    // });
    //
    // skip.setOnAction(event -> {
    // currentMeta.set(null);
    // try {
    // choiceBarrier.await();
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // } catch (BrokenBarrierException e) {
    // // TODO what?
    // }
    // stage.setTitle("Mulima: Waiting for next choice");
    // table.getItems().clear();
    // });
    //
    // var scene = new Scene(pane, 960, 1080);
    // stage.setScene(scene);
    // stage.show();
    //
    // mulima.start();
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
