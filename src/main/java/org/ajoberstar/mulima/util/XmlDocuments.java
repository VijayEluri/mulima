package org.ajoberstar.mulima.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class XmlDocuments {
  private XmlDocuments() {
    // don't instantiate
  }

  public static Document parse(Path file) {
    try {
      return parse(Files.newInputStream(file));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Document parse(InputStream stream) {
    try {
      var builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      return builder.parse(stream);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      // TODO better
      throw new RuntimeException(e);
    }
  }

  public static void write(Document doc, Path file) {
    try {
      var tFactory = TransformerFactory.newInstance();
      var transformer = tFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      var source = new DOMSource(doc);
      try (var stream = Files.newOutputStream(file)) {
        var result = new StreamResult(stream);
        transformer.transform(source, result);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } catch (TransformerException e) {
      // TODO better
      throw new RuntimeException(e);
    }
  }

  public static Stream<Node> stream(NodeList nodes) {
    return IntStream.range(0, nodes.getLength())
        .mapToObj(nodes::item);
  }

  public static Stream<Node> getChildren(Node node, String... path) {
    if (path.length == 0) {
      return Stream.of(node);
    } else {
      var currentPath = path[0];
      var restPath = Arrays.stream(path).skip(1).toArray(String[]::new);
      if (currentPath.startsWith("@")) {
        var attrNode = node.getAttributes().getNamedItem(currentPath.substring(1));
        return Stream.of(attrNode);
      } else {
        return stream(node.getChildNodes())
            .filter(child -> currentPath.equals(child.getNodeName()))
            .flatMap(child -> getChildren(child, restPath));
      }
    }
  }

  public static Optional<String> getText(Node node, String... path) {
    return getChildren(node, path)
        .map(Node::getTextContent)
        .findFirst();
  }

  public static String getAttribute(Node node, String attr) {
    return node.getAttributes().getNamedItem(attr).getTextContent();
  }
}
