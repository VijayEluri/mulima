package org.ajoberstar.mulima.audio;

import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

public interface AudioDecoder {
  boolean acceptsDecode(Path source, Path destination);

  CompletionStage<Void> decode(Path source, Path destination);
}
