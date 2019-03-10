package org.ajoberstar.mulima.audio;

import java.nio.file.Path;

public interface AudioEncoder {
  boolean acceptsEncode(Path source, Path destination);

  void encode(Path source, Path destination);
}
