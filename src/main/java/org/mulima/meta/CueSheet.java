package org.mulima.meta;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Default implementation of a cue sheet.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class CueSheet extends AbstractMetadata implements Metadata, Comparable<CueSheet> {
  private int num;
  private final SortedSet<CuePoint> cuePoints = new TreeSet<CuePoint>();

  /** Constructs an unnumbered cue sheet. */
  public CueSheet() {
    super(null);
  }

  /**
   * Constructs a numbered cue sheet.
   *
   * @param num the disc number
   */
  public CueSheet(int num) {
    super(null);
    this.num = num;
  }

  /**
   * Gets the disc number of this cue sheet.
   *
   * @return the disc number
   */
  public int getNum() {
    return num;
  }

  /**
   * Gets all cue points that correspond to a track's start. (i.e. all points with index 1)
   *
   * @return all index 1 points
   */
  public SortedSet<CuePoint> getCuePoints() {
    SortedSet<CuePoint> points = new TreeSet<CuePoint>();
    for (CuePoint point : getAllCuePoints()) {
      if (point.getIndex() == 1) {
        points.add(point);
      }
    }
    return Collections.unmodifiableSortedSet(points);
  }

  /**
   * Gets all cue points for this sheet.
   *
   * @return all cue points
   */
  public SortedSet<CuePoint> getAllCuePoints() {
    return cuePoints;
  }

  /** {@inheritDoc} */
  @Override
  public void tidy() {
    // no-op
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(CueSheet o) {
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
    } else if (obj instanceof CueSheet) {
      CueSheet that = (CueSheet) obj;
      return this.getNum() == that.getNum()
          && this.getMap().equals(that.getMap())
          && this.getAllCuePoints().equals(that.getAllCuePoints());
    } else {
      return false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int result = 23;
    result = result * 31 + getNum();
    result = result * 31 + getMap().hashCode();
    result = result * 31 + getAllCuePoints().hashCode();
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[num:");
    builder.append(getNum());
    builder.append(", tags:");
    builder.append(getMap());
    builder.append(", points:");
    builder.append(getAllCuePoints());
    builder.append("]");
    return builder.toString();
  }
}
