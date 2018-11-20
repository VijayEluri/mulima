package org.mulima.api.meta;

/**
 * An object representing a track.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface Track extends Metadata, Comparable<Track> {
  /**
   * Gets the track number.
   *
   * @return the track number
   */
  int getNum();

  /**
   * Gets the disc number.
   *
   * @return the disc number
   */
  int getDiscNum();

  /**
   * Gets the start point of this track.
   *
   * @return the start point
   */
  CuePoint getStartPoint();

  /**
   * Sets the start point of this track.
   *
   * @param startPoint the start point
   */
  void setStartPoint(CuePoint startPoint);

  /**
   * Gets the end point of this track.
   *
   * @return the end point
   */
  CuePoint getEndPoint();

  /**
   * Sets the end point of this track.
   *
   * @param endPoint the end point
   */
  void setEndPoint(CuePoint endPoint);
}
