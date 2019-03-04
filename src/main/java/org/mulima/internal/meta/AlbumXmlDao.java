package org.mulima.internal.meta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.SortedSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.api.file.FileComposer;
import org.mulima.api.file.FileParser;
import org.mulima.api.meta.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class AlbumXmlDao implements FileParser<Album>, FileComposer<Album> {
  private final Logger logger = LogManager.getLogger(AlbumXmlDao.class);

  public Album parse(File file) {
    try {
      var factory = DocumentBuilderFactory.newInstance();
      var builder = factory.newDocumentBuilder();
      var doc = builder.parse(file);
      var album = new DefaultAlbum();
      parseTags(getChildren(doc, "tag"), album);
      parseDiscs(getChildren(doc, "disc"), album);
      album.tidy();
      return album;
    } catch (ParserConfigurationException | SAXException | IOException e) {
      logger.error("Problem reading file: {}", file.getAbsolutePath(), e);
      return null;
    }
  }

  private void parseDiscs(Stream<Node> nodes, Album album) {
    nodes.forEach(node -> {
      var disc = new DefaultDisc(album);
      parseTags(getChildren(node, "tag"), disc);
      parseTracks(getChildren(node, "track"), disc);
      album.getDiscs().add(disc);
    });
  }

  private void parseTracks(Stream<Node> nodes, Disc disc) {
    nodes.forEach(node -> {
      var track = new DefaultTrack(disc);
      parseTags(getChildren(node, "tag"), track);
      track.setStartPoint(parseCuePoint(getChildren(node, "startPoint")));
      track.setEndPoint(parseCuePoint(getChildren(node, "endPoint")));
      disc.getTracks().add(track);
    });
  }

  private CuePoint parseCuePoint(Stream<Node> nodes) {
    return nodes.map(node -> {
      var track = Integer.parseInt(node.getAttributes().getNamedItem("track").getTextContent());
      var index = Integer.parseInt(node.getAttributes().getNamedItem("index").getTextContent());
      var time = node.getAttributes().getNamedItem("time").getTextContent();
      return new DefaultCuePoint(track, index, time);
    }).findAny().orElse(null);
  }

  private void parseTags(Stream<Node> nodes, Metadata meta) {
    nodes.forEach(node -> {
      var tag = GenericTag.valueOfCamelCase(node.getAttributes().getNamedItem("name").getTextContent());
      var value = node.getAttributes().getNamedItem("value").getTextContent();
      meta.add(tag, value);
    });
  }

  private Stream<Node> getChildren(Node node, String elementName) {
    var children = node.getChildNodes();
    return IntStream.range(0, children.getLength())
        .mapToObj(children::item)
        .filter(child -> elementName.equals(child.getNodeName()));
  }

  public void compose(File file, Album album) {
    try {
      album.tidy();
      var factory = DocumentBuilderFactory.newInstance();
      var builder = factory.newDocumentBuilder();
      var doc = builder.newDocument();

      var albumElem = doc.createElement("album");
      composeTags(doc, albumElem, album);
      composeDiscs(doc, albumElem, album.getDiscs());

      var tFactory = TransformerFactory.newInstance();
      var transformer = tFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      var source = new DOMSource(doc);
      try (var writer = new FileWriter(file)) {
        var result = new StreamResult(writer);
        transformer.transform(source, result);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } catch (ParserConfigurationException | TransformerException e) {
      logger.error("Problem writing file: {}", file.getAbsolutePath(), e);
    }
  }

  private void composeTags(Document doc, Node parent, Metadata meta) {
    meta.getMap().forEach((tag, values) -> {
      values.forEach(value -> {
        var elem = doc.createElement("tag");
        elem.setAttribute("name", tag.camelCase());
        elem.setAttribute("value", value);
        parent.appendChild(elem);
      });
    });
  }

  private void composeDiscs(Document doc, Node parent, SortedSet<Disc> discs) {
    discs.forEach(disc -> {
      var elem = doc.createElement("disc");
      composeTags(doc, elem, disc);
      composeTracks(doc, elem, disc.getTracks());
      parent.appendChild(elem);
    });
  }

  private void composeTracks(Document doc, Node parent, SortedSet<Track> tracks) {
    tracks.forEach(track -> {
      var elem = doc.createElement("track");
      composeTags(doc, elem, track);
      if (track.getStartPoint() != null) {
        var cue = doc.createElement("startPoint");
        cue.setAttribute("track", Integer.toString(track.getStartPoint().getTrack()));
        cue.setAttribute("track", Integer.toString(track.getStartPoint().getIndex()));
        cue.setAttribute("time", track.getStartPoint().getTime());
      }
      if (track.getEndPoint() != null) {
        var cue = doc.createElement("endPoint");
        cue.setAttribute("track", Integer.toString(track.getEndPoint().getTrack()));
        cue.setAttribute("track", Integer.toString(track.getEndPoint().getIndex()));
        cue.setAttribute("time", track.getEndPoint().getTime());
      }
      parent.appendChild(elem);
    });
  }
}
