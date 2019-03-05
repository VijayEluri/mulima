package org.mulima.meta;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Default implementation of a disc.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class Disc extends AbstractMetadata implements Metadata, Comparable<Disc> {
  private final SortedSet<Track> tracks = new TreeSet<>();

  public Disc() {
    super(null);
  }

  public Disc(Album album) {
    super(album);
  }

  /**
   * Gets the disc number.
   *
   * @return the disc number
   */
  public int getNum() {
    return Integer.valueOf(getFirst(GenericTag.DISC_NUMBER));
  }

  /**
   * Gets the tracks that are part of this disc.
   *
   * @return the tracks
   */
  public SortedSet<Track> getTracks() {
    return tracks;
  }

  /**
   * Gets the track specified by the parameter.
   *
   * @param num the number of the track to get
   * @return the track or {@code null} if it could not be found
   */
  public Track getTrack(int num) {
    for (var track : tracks) {
      if (track.getNum() == num) {
        return track;
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public void tidy() {
    tidy(getTracks());
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(Disc o) {
    if (this.equals(o)) {
      return 0;
    } else if (getNum() == o.getNum()) {
      return 1;
    } else {
      return getNum() < o.getNum() ? -1 : 1;
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof Disc) {
      var that = (Disc) obj;
      return this.getMap().equals(that.getMap()) && this.getTracks().equals(that.getTracks());
    } else {
      return false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    var result = 23;
    result = result * 31 + getMap().hashCode();
    result = result * 31 + getTracks().hashCode();
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    var builder = new StringBuilder();
    builder.append("[tags:");
    builder.append(getMap());
    builder.append(", tracks:");
    builder.append(getTracks());
    builder.append("]");
    return builder.toString();
  }
}
