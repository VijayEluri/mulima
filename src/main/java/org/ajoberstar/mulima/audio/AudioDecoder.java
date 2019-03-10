package org.ajoberstar.mulima.audio;

import java.nio.file.Path;

public interface AudioDecoder {
  boolean acceptsDecode(Path source, Path destination);

  void decode(Path source, Path destination);
}
