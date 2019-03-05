package org.mulima.meta;

/**
 * Default implementation of a track.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class Track extends AbstractMetadata implements Metadata, Comparable<Track> {
  private CuePoint startPoint = null;
  private CuePoint endPoint = null;

  public Track() {
    super(null);
  }

  public Track(Disc disc) {
    super(disc);
  }

  /**
   * Gets the track number.
   *
   * @return the track number
   */
  public int getNum() {
    return Integer.valueOf(getFirst(GenericTag.TRACK_NUMBER));
  }

  /**
   * Gets the disc number.
   *
   * @return the disc number
   */
  public int getDiscNum() {
    try {
      return Integer.valueOf(getFirst(GenericTag.DISC_NUMBER));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Gets the start point of this track.
   *
   * @return the start point
   */
  public CuePoint getStartPoint() {
    return startPoint;
  }

  /**
   * Sets the start point of this track.
   *
   * @param startPoint the start point
   */
  public void setStartPoint(CuePoint startPoint) {
    this.startPoint = startPoint;
  }

  /**
   * Gets the end point of this track.
   *
   * @return the end point
   */
  public CuePoint getEndPoint() {
    return endPoint;
  }

  /**
   * Sets the end point of this track.
   *
   * @param endPoint the end point
   */
  public void setEndPoint(CuePoint endPoint) {
    this.endPoint = endPoint;
  }

  /** {@inheritDoc} */
  @Override
  public void tidy() {
    // no-op
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(Track o) {
    if (this.equals(o)) {
      return 0;
    } else if (getDiscNum() == o.getDiscNum()) {
      if (getNum() == o.getNum()) {
        return 1;
      } else {
        return getNum() < o.getNum() ? -1 : 1;
      }
    } else {
      return getDiscNum() < o.getDiscNum() ? -1 : 1;
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof Track) {
      Track that = (Track) obj;
      return this.getMap().equals(that.getMap())
          && ((this.getStartPoint() == null && that.getStartPoint() == null)
              || (this.getStartPoint().equals(that.getStartPoint())))
          && ((this.getEndPoint() == null && that.getEndPoint() == null)
              || (this.getEndPoint().equals(that.getEndPoint())));
    } else {
      return false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int result = 23;
    result = result * 31 + getMap().hashCode();
    result = result * 31 + getStartPoint().hashCode();
    result = result * 31 + getEndPoint().hashCode();
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[tags:");
    builder.append(getMap());
    builder.append(", start:");
    builder.append(getStartPoint());
    builder.append(", end:");
    builder.append(getEndPoint());
    builder.append("]");
    return builder.toString();
  }
}
