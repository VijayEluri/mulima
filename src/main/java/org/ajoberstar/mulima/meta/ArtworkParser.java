package org.ajoberstar.mulima.meta;

import java.nio.file.Path;

public class ArtworkParser implements MetadataParser {
  @Override
  public boolean accepts(Path file) {
    return file.endsWith("folder.jpg") || file.endsWith("folder.png");
  }

  @Override
  public Metadata parse(Path file) {
    return Metadata.builder("generic")
        .setSourceFile(file)
        .setArtworkFile(file)
        .build();
  }
}
