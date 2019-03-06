package org.mulima.future.meta;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class AlbumXmlParser implements MetadataParser {
  private static final Logger logger = LogManager.getLogger(AlbumXmlParser.class);

  @Override
  public boolean accepts(Path file) {
    return file.endsWith("album.xml");
  }

  @Override
  public Metadata parse(Path file) {
    try(var input = Files.newInputStream(file)) {
      var factory = DocumentBuilderFactory.newInstance();
      var builder = factory.newDocumentBuilder();
      var doc = builder.parse(input);
      // TODO better exception
      var album = getChildren(doc, "album").findAny().orElseThrow(() -> new RuntimeException("Invalid album.xml. No root <album> element."));

      var metadata = Metadata.builder("album-xml");
      metadata.setFile(file);

      parseTags(getChildren(album, "tag"), metadata);
      parseDiscs(getChildren(album, "disc"), metadata);
      return metadata.build();
    } catch (ParserConfigurationException | SAXException | IOException e) {
      logger.error("Problem reading file: {}", file.toAbsolutePath(), e);
      // TODO better exception
      throw new RuntimeException(e);
    }
  }

  private void parseDiscs(Stream<Node> nodes, Metadata.Builder album) {
    nodes.forEach(node -> {
      var disc = album.newChild();
      parseTags(getChildren(node, "tag"), disc);
      parseTracks(getChildren(node, "track"), disc);
    });
  }

  private void parseTracks(Stream<Node> nodes, Metadata.Builder disc) {
    nodes.forEach(node -> {
      var track = disc.newChild();
      parseTags(getChildren(node, "tag"), track);
      parseCuePoint(getChildren(node, "startPoint"), track);
      parseCuePoint(getChildren(node, "endPoint"), track);
    });
  }

  private void parseCuePoint(Stream<Node> nodes, Metadata.Builder track) {
    nodes.forEach(node -> {
      var index = Integer.parseInt(node.getAttributes().getNamedItem("index").getTextContent());
      var time = node.getAttributes().getNamedItem("time").getTextContent();
      track.addCue(new CuePoint(index, time));
    });
  }

  private void parseTags(Stream<Node> nodes, Metadata.Builder meta) {
    nodes.forEach(node -> {
      var tag = node.getAttributes().getNamedItem("name").getTextContent();
      var value = node.getAttributes().getNamedItem("value").getTextContent();
      meta.addTag(tag, value);
    });
  }

  private Stream<Node> getChildren(Node node, String elementName) {
    var children = node.getChildNodes();
    return IntStream.range(0, children.getLength())
        .mapToObj(children::item)
        .filter(child -> elementName.equals(child.getNodeName()));
  }
}
