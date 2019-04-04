package org.ajoberstar.mulima.meta;

import java.nio.file.Path;

public interface MetadataParser {
  Metadata parse(Path file);
}
