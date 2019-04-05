package org.ajoberstar.mulima.audio;

import org.ajoberstar.mulima.meta.Album;
import org.ajoberstar.mulima.meta.Metadata;

import java.nio.file.Path;
import java.util.List;

public interface AudioSplitter {
  boolean acceptsSplit(Path source);

  List<Path> split(Album album, List<Metadata> metadata, Path destinationDirectory);
}
