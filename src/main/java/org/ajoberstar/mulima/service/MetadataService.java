package org.ajoberstar.mulima.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ajoberstar.mulima.meta.AlbumXmlParser;
import org.ajoberstar.mulima.meta.ArtworkParser;
import org.ajoberstar.mulima.meta.CueSheetParser;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.meta.MetadataParser;
import org.ajoberstar.mulima.meta.MetadataWriter;
import org.ajoberstar.mulima.meta.MetadataYaml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MetadataService {
  private static final Logger logger = LogManager.getLogger(MetadataService.class);

  private final AlbumXmlParser albumXmlParser;
  private final ArtworkParser artworkParser;
  private final CueSheetParser cueSheetParser;
  private final MetadataYaml metaYaml;

  private final List<MetadataParser> parsers;
  private final List<MetadataWriter> writers;

  public MetadataService(List<MetadataParser> parsers, List<MetadataWriter> writers) {
    this.albumXmlParser = parsers.stream()
        .filter(AlbumXmlParser.class::isInstance)
        .map(AlbumXmlParser.class::cast)
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Must provide album.xml parser"));
    this.artworkParser = parsers.stream()
        .filter(ArtworkParser.class::isInstance)
        .map(ArtworkParser.class::cast)
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Must provide artwork parser"));
    this.cueSheetParser = parsers.stream()
        .filter(CueSheetParser.class::isInstance)
        .map(CueSheetParser.class::cast)
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Must provide .cue parser"));
    this.metaYaml = parsers.stream()
        .filter(MetadataYaml.class::isInstance)
        .map(MetadataYaml.class::cast)
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Must provide .yaml parser"));
    this.parsers = parsers;
    this.writers = writers;
  }

  public Metadata parseFile(Path file) {
    return parsers.stream()
        .filter(parser -> parser.accepts(file))
        .map(parser -> parser.parse(file))
        .map(meta -> meta.translate("generic"))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No parser available for: " + file.getFileName()));
  }

  public Metadata parseDir(Path dir) {
    try (var fileStream = Files.list(dir)) {
      var files = fileStream.collect(Collectors.toList());

      var artwork = parseDirFor(files, artworkParser);
      var cues = parseDirFor(files, cueSheetParser);
      var xmls = parseDirFor(files, albumXmlParser);
      var yamls = parseDirFor(files, metaYaml);

      var builder = Metadata.builder("generic");
      builder.setSourceFile(dir);

      // there's probably only one artwork, so this is more code than it should be
      artwork.stream()
          .map(Metadata::getAudioFile)
          .flatMap(Optional::stream)
          .findFirst()
          .ifPresent(builder::setArtworkFile);

      // these are handled in preference order
      if (!yamls.isEmpty()) {
        if (yamls.size() > 1) {
          // TODO better
          throw new RuntimeException("Dir contains multiple .yaml files: " + dir);
        }
        yamls.forEach(builder::addChild);
      } else if (!cues.isEmpty()) {
        cues.forEach(builder::addChild);
      } else if (!xmls.isEmpty()) {
        if (xmls.size() > 1) {
          // TODO better
          throw new RuntimeException("Dir contains multiple album.xml files: " + dir);
        }
        xmls.forEach(builder::addChild);
      }
      return builder.build().denormalize();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private List<Metadata> parseDirFor(List<Path> dirFiles, MetadataParser parser) {
    return dirFiles.stream()
        .filter(parser::accepts)
        .map(parser::parse)
        .collect(Collectors.toList());
  }

  public void writeFile(Metadata meta, Path file) {
    var writer = writers.stream()
        .filter(w -> w.accepts(file))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No writers available for: " + file.getFileName()));
    writer.write(meta, file);
  }
}
