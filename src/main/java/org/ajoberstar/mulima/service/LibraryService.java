package org.ajoberstar.mulima.service;

import org.ajoberstar.mulima.audio.FlacCodec;
import org.ajoberstar.mulima.audio.OpusEncoder;
import org.ajoberstar.mulima.meta.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class LibraryService {
  private static final Logger logger = LogManager.getLogger(LibraryService.class);

  private final MetadataService metadata;
  private final MusicBrainzService musicbrainz;
  private final FlacCodec flac;
  private final OpusEncoder opusenc;

  public LibraryService(MetadataService metadata, MusicBrainzService musicbrainz, FlacCodec flac, OpusEncoder opusenc) {
    this.metadata = metadata;
    this.musicbrainz = musicbrainz;
    this.flac = flac;
    this.opusenc = opusenc;
  }

  public Map<Path, List<Metadata>> scan(Path sourceDir) {
    var allMetadata = metadata.parseDirRecursive(sourceDir).toCompletableFuture().join();
    return allMetadata.stream()
        .filter(meta -> meta.getSourceFile().isPresent())
        .collect(Collectors.groupingBy(metadata -> metadata.getSourceFile().map(Path::getParent).get()));
  }

  public Metadata merge(List<Metadata> metadata) {
    // TODO implement
    return null;
  }

  public void convert(Path sourceDir, Path losslessDestDir, Path lossyDestDir) {

  }

  public void handleDirectory(Path albumDir) {
    // try {
    // Files.
    //
    // Optional<CompletionStage<Metadata>> albumXml = Files.list(albumDir)
    // .filter(albumXmlParser::accepts)
    // .map(albumXmlParser::parse)
    // .findAny();
    //
    // Optional<CompletionStage<Metadata>> cueSheet = Files.list(albumDir)
    // .filter(cueSheetParser::accepts)
    // .map(cueSheetParser::parse)
    // .findAny();
    //
    // var artwork = Files.list(albumDir)
    // .
    // } catch (IOException e) {
    // throw new UncheckedIOException(e);
    // }

  }

  public boolean isUpToDate(Metadata album) {
    // TODO implement
    return false;
  }
}
