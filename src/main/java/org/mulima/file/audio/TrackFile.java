package org.mulima.file.audio;

import java.io.File;

import org.mulima.meta.Metadata;
import org.mulima.meta.Track;

/**
 * Default implementation of a track file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class TrackFile extends AbstractAudioFile implements AudioFile {
  private Track track;
  private int discNum;
  private int trackNum;

  /**
   * Constructs a track file from the parameters.
   *
   * @param file the file
   * @param track the metadata for the file
   */
  public TrackFile(File file, Track track) {
    super(file);
    if (track == null) {
      throw new NullPointerException("Track cannot be null.");
    }
    this.track = track;
    this.discNum = -1;
    this.trackNum = -1;
  }

  /**
   * Constructs a track file from the parameters.
   *
   * @param file the file
   * @param discNum the disc number
   * @param trackNum the track number
   */
  public TrackFile(File file, int discNum, int trackNum) {
    super(file);
    if (discNum < 0) {
      throw new IllegalArgumentException("Disc cannot be less than zero.");
    }
    this.discNum = discNum;
    if (trackNum < 0) {
      throw new IllegalArgumentException("Track cannot be less than zero.");
    }
    this.trackNum = trackNum;
    this.track = null;
  }

  /**
   * Gets the disc number of this file.
   *
   * @return the disc number
   */
  public int getDiscNum() {
    if (track == null) {
      return discNum;
    } else {
      return track.getDiscNum();
    }
  }

  /**
   * Gets the track number of this file.
   *
   * @return the track number
   */
  public int getTrackNum() {
    if (track == null) {
      return trackNum;
    } else {
      return track.getNum();
    }
  }

  /** {@inheritDoc} */
  @Override
  public Track getMeta() {
    return track;
  }

  /** {@inheritDoc} */
  @Override
  public void setMeta(Metadata meta) {
    if (meta instanceof Track) {
      this.track = (Track) meta;
    } else {
      throw new IllegalArgumentException("TrackFiles only accept Track metadata.");
    }
  }
}
