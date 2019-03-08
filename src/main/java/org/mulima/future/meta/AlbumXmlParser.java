package org.mulima.future.meta;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.future.util.XmlDocuments;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class AlbumXmlParser implements MetadataParser {
  private static final Logger logger = LogManager.getLogger(AlbumXmlParser.class);

  private final ExecutorService executor;

  public AlbumXmlParser(ExecutorService executor) {
    this.executor = executor;
  }

  @Override
  public boolean accepts(Path file) {
    return file.endsWith("album.xml");
  }

  @Override
  public CompletionStage<Metadata> parse(Path file) {
    return CompletableFuture.supplyAsync(() -> {
      var doc = XmlDocuments.parse(file);
      // TODO better exception
      var album = XmlDocuments.getChildren(doc, "album").findAny().orElseThrow(() -> new RuntimeException("Invalid album.xml. No root <album> element."));

      var metadata = Metadata.builder("album-xml");
      metadata.setFile(file);

      parseTags(XmlDocuments.getChildren(album, "tag"), metadata);
      parseDiscs(XmlDocuments.getChildren(album, "disc"), metadata);
      return metadata.build();
    }, executor);
  }

  private void parseDiscs(Stream<Node> nodes, Metadata.Builder album) {
    nodes.forEach(node -> {
      var disc = album.newChild();
      parseTags(XmlDocuments.getChildren(node, "tag"), disc);
      parseTracks(XmlDocuments.getChildren(node, "track"), disc);
    });
  }

  private void parseTracks(Stream<Node> nodes, Metadata.Builder disc) {
    nodes.forEach(node -> {
      var track = disc.newChild();
      parseTags(XmlDocuments.getChildren(node, "tag"), track);
      parseCuePoint(XmlDocuments.getChildren(node, "startPoint"), track);
      parseCuePoint(XmlDocuments.getChildren(node, "endPoint"), track);
    });
  }

  private void parseCuePoint(Stream<Node> nodes, Metadata.Builder track) {
    nodes.forEach(node -> {
      var index = Integer.parseInt(XmlDocuments.getAttribute(node, "index"));
      var time = XmlDocuments.getAttribute(node, "time");
      track.addCue(new CuePoint(index, time));
    });
  }

  private void parseTags(Stream<Node> nodes, Metadata.Builder meta) {
    nodes.forEach(node -> {
      var tag = XmlDocuments.getAttribute(node, "name");
      var value = XmlDocuments.getAttribute(node, "value");
      meta.addTag(tag, value);
    });
  }
}
