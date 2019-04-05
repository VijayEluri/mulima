package org.ajoberstar.mulima.audio;

import java.nio.file.Path;
import java.util.List;

import org.ajoberstar.mulima.meta.Album;
import org.ajoberstar.mulima.meta.Metadata;

public interface AudioSplitter {
  boolean acceptsSplit(Path source);

  List<Path> split(Album album, List<Metadata> metadata, Path destinationDirectory);
}
