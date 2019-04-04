package org.ajoberstar.mulima.meta;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class Album {
  private final Path dir;
  private final List<Path> artwork;
  private final Map<Path, List<CuePoint>> audioToCues;
  private final Map<Path, Metadata> audioToMetadata;

  public Album(Path dir, List<Path> artwork, Map<Path, List<CuePoint>> audioToCues, Map<Path, Metadata> audioToMetadata) {
    this.dir = dir;
    this.artwork = artwork;
    this.audioToCues = audioToCues;
    this.audioToMetadata = audioToMetadata;
  }

  public Path getDir() {
    return dir;
  }

  public List<Path> getArtwork() {
    return artwork;
  }

  public Map<Path, List<CuePoint>> getAudioToCues() {
    return audioToCues;
  }

  public Map<Path, Metadata> getAudioToMetadata() {
    return audioToMetadata;
  }

  @Override
  public boolean equals(Object that) {
    return EqualsBuilder.reflectionEquals(this, that);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
