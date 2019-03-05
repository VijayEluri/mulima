package org.mulima.meta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of a cue point.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class CuePoint implements Comparable<CuePoint> {
  private static final Pattern FRAMES_REGEX = Pattern.compile("^(\\d+):(\\d{2}):(\\d{2})$");
  private static final Pattern TIME_REGEX = Pattern.compile("^(\\d+):(\\d{2})\\.(\\d{3})$");

  private final int track;
  private final int index;
  private final String time;

  /**
   * Constructs a cue point with the parameters.
   *
   * @param track the track number
   * @param index the index number
   * @param time the timecode
   */
  public CuePoint(int track, int index, String time) {
    if (track > 0) {
      this.track = track;
    } else {
      throw new IllegalArgumentException("Track number must be greater than 0.");
    }

    if (index >= 0) {
      this.index = index;
    } else {
      throw new IllegalArgumentException("Index number must be 0 or greater.");
    }

    if (verifyTimeFormat(time) || verifyFramesFormat(time)) {
      this.time = time;
    } else {
      throw new IllegalArgumentException(
          "Time must match one of the following formats: "
              + TIME_REGEX.pattern()
              + " or "
              + FRAMES_REGEX.pattern());
    }
  }

  /**
   * Verifies that the time string matches the expected format.
   *
   * @param timeStr the string to verify
   * @return {@code true} if it is valid, {@code false} otherwise
   */
  private boolean verifyFramesFormat(String timeStr) {
    Matcher matcher = FRAMES_REGEX.matcher(timeStr);
    if (matcher.find()) {
      int minutes = Integer.valueOf(matcher.group(1));
      if (minutes < 0) {
        return false;
      }
      int seconds = Integer.valueOf(matcher.group(2));
      if (seconds < 0 || seconds >= 60) {
        return false;
      }
      int frames = Integer.valueOf(matcher.group(3));
      if (frames < 0 || frames >= 75) {
        return false;
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Verifies that the time string matches the expected format.
   *
   * @param timeStr the string to verify
   * @return {@code true} if it is valid, {@code false} otherwise
   */
  private boolean verifyTimeFormat(String timeStr) {
    Matcher matcher = TIME_REGEX.matcher(timeStr);
    if (matcher.find()) {
      int minutes = Integer.valueOf(matcher.group(1));
      if (minutes < 0) {
        return false;
      }
      int seconds = Integer.valueOf(matcher.group(2));
      if (seconds < 0 || seconds >= 60) {
        return false;
      }
      int parts = Integer.valueOf(matcher.group(3));
      if (parts < 0) {
        return false;
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Gets the number of the track this point is for.
   *
   * @return the track number
   */
  public int getTrack() {
    return track;
  }

  /**
   * Gets the index number of this point.
   *
   * @return the index number
   */
  public int getIndex() {
    return index;
  }

  /**
   * Gets the timecode for this point.
   *
   * @return the timecode
   */
  public String getTime() {
    return time;
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(CuePoint other) {
    if (track == other.getTrack()) {
      if (index == other.getIndex()) {
        if (time.equals(other.getTime())) {
          return 0;
        } else {
          return time.compareTo(other.getTime());
        }
      } else {
        return index < other.getIndex() ? -1 : 1;
      }
    } else {
      return track < other.getTrack() ? -1 : 1;
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof CuePoint) {
      CuePoint that = (CuePoint) obj;
      return this.getTrack() == that.getTrack()
          && this.getIndex() == that.getIndex()
          && this.getTime().equals(that.getTime());
    } else {
      return false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int result = 23;
    result = result * 31 + getTrack();
    result = result * 31 + getIndex();
    result = result * 31 + getTime().hashCode();
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[track:");
    builder.append(getTrack());
    builder.append(", index:");
    builder.append(getIndex());
    builder.append(", time:");
    builder.append(getTime());
    builder.append("]");
    return builder.toString();
  }
}