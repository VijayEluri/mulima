package org.ajoberstar.mulima.meta;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.regex.Pattern;

public final class CuePoint implements Comparable<CuePoint> {
  private static final Pattern FRAMES_REGEX = Pattern.compile("^(\\d+):(\\d{2}):(\\d{2})$");

  private final int index;
  private final String time;

  private final int minutes;
  private final int seconds;
  private final int frames;

  public CuePoint(int index, String time) {
    if (index >= 0) {
      this.index = index;
    } else {
      throw new IllegalArgumentException("Index number must be 0 or greater.");
    }

    this.time = time;
    var matcher = FRAMES_REGEX.matcher(time);
    if (matcher.find()) {
      this.minutes = Integer.valueOf(matcher.group(1));
      this.seconds = Integer.valueOf(matcher.group(2));
      this.frames = Integer.valueOf(matcher.group(3));
    } else {
      throw new IllegalArgumentException("Time must match " + FRAMES_REGEX.pattern());
    }
  }

  public int getIndex() {
    return index;
  }

  public String getTime() {
    return time;
  }

  public int getOffset() {
    var totalSeconds = 60 * minutes + seconds;
    return 75 * totalSeconds + frames + 150;
  }

  @Override
  public int compareTo(CuePoint that) {
    return CompareToBuilder.reflectionCompare(this, that);
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
