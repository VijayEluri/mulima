package org.ajoberstar.mulima.meta;

import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

public interface MetadataParser {
  boolean accepts(Path file);

  CompletionStage<Metadata> parse(Path file);
}
