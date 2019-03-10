package org.ajoberstar.mulima.meta;

import java.nio.file.Path;

public interface MetadataWriter {
  boolean accepts(Path file);

  void write(Metadata meta, Path file);
}
