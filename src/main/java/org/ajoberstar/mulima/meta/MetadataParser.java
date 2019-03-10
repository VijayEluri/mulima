package org.ajoberstar.mulima.meta;

import java.nio.file.Path;

public interface MetadataParser {
  boolean accepts(Path file);

  Metadata parse(Path file);
}
