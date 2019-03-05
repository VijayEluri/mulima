package org.mulima.meta;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Default implementation of an album.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class Album extends AbstractMetadata implements Metadata {
  /** The standard name for an album file. */
  public static final String FILE_NAME = "album.xml";
  private final SortedSet<Disc> discs = new TreeSet<>();

  public Album() {
    super(null);
  }

  /**
   * Gets the discs that are part of this album.
   *
   * @return the discs
   */
  public SortedSet<Disc> getDiscs() {
    return discs;
  }

  /**
   * Gets the disc specified by the parameter.
   *
   * @param num the number of the disc to get
   * @return the disc or {@code null} if not found
   */
  public Disc getDisc(int num) {
    for (var disc : discs) {
      if (disc.getNum() == num) {
        return disc;
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public void tidy() {
    tidy(getDiscs());
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof Album) {
      var that = (Album) obj;
      return this.getMap().equals(that.getMap()) && this.getDiscs().equals(that.getDiscs());
    } else {
      return false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    var result = 23;
    result = result * 31 + getMap().hashCode();
    result = result * 31 + getDiscs().hashCode();
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    var builder = new StringBuilder();
    builder.append("[tags:");
    builder.append(getMap());
    builder.append(", discs:");
    builder.append(getDiscs());
    builder.append("]");
    return builder.toString();
  }
}
