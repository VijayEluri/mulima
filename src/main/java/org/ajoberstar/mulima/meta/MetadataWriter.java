package org.ajoberstar.mulima.meta;

import java.nio.file.Path;

public interface MetadataWriter {
  void write(Metadata meta, Path file);
}
