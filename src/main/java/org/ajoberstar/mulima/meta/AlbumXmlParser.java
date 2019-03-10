package org.ajoberstar.mulima.meta;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.ajoberstar.mulima.util.XmlDocuments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

public final class AlbumXmlParser implements MetadataParser {
  private static final Logger logger = LogManager.getLogger(AlbumXmlParser.class);
  private static final Pattern DISC_AUDIO_FILE =Pattern.compile("D(\\d+)\\.flac|.*\\((\\d+)\\)\\.flac");

  @Override
  public boolean accepts(Path file) {
    return file.endsWith("album.xml");
  }

  @Override
  public Metadata parse(Path file) {
    var doc = XmlDocuments.parse(file);
    // TODO better exception
    var album = XmlDocuments.getChildren(doc, "album").findAny().orElseThrow(() -> new RuntimeException("Invalid album.xml. No root <album> element."));

    var metadata = Metadata.builder("album-xml");
    metadata.setSourceFile(file);

    parseTags(XmlDocuments.getChildren(album, "tag"), metadata);
    parseDiscs(XmlDocuments.getChildren(album, "disc"), metadata);
    return metadata.build();
  }

  private void parseDiscs(Stream<Node> nodes, Metadata.Builder album) {
    nodes.forEach(node -> {
      var disc = album.newChild();
      parseTags(XmlDocuments.getChildren(node, "tag"), disc);
      parseTracks(XmlDocuments.getChildren(node, "track"), disc);

      try (var fileStream = Files.list(album.getSourceFile().getParent())) {
        Function<MatchResult, Integer> toDiscNum = result -> Optional.ofNullable(result.group(1))
            .or(() -> Optional.ofNullable(result.group(2)))
            .map(Integer::parseInt)
            .orElse(1);

        var discNum = disc.getTags().getOrDefault("discNumber", List.of()).stream()
            .map(Integer::parseInt)
            .findAny()
            .orElse(1);

        Predicate<Path> isFileForDisc = f -> {
          var num = DISC_AUDIO_FILE.matcher(f.getFileName().toString()).results()
              .map(toDiscNum)
              .findAny()
              .orElse(1);
          return num == discNum;
        };

        fileStream
            .filter(f -> f.getFileName().toString().endsWith(".flac"))
            .filter(isFileForDisc)
            .findAny()
            .ifPresent(disc::setAudioFile);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
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
