package org.ajoberstar.mulima.audio;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.ajoberstar.mulima.meta.Metadata;

public interface AudioSplitter {
  boolean acceptsSplit(Path source);

  CompletionStage<List<Metadata>> split(Metadata meta, Path source, Path destinationDirectory);
}
