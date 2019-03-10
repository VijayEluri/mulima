package org.ajoberstar.mulima.audio;

import java.nio.file.Path;

import org.ajoberstar.mulima.meta.Metadata;

public interface AudioSplitter {
  boolean acceptsSplit(Path source);

  Metadata split(Metadata meta, Path destinationDirectory);
}
