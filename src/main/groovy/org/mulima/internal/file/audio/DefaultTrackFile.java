/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mulima.internal.file.audio;

import java.io.File;

import org.mulima.api.file.audio.TrackFile;
import org.mulima.api.meta.Metadata;
import org.mulima.api.meta.Track;

/**
 * Default implementation of a track file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DefaultTrackFile extends AbstractAudioFile implements TrackFile {
  private Track track;
  private int discNum;
  private int trackNum;

  /**
   * Constructs a track file from the parameters.
   *
   * @param file the file
   * @param track the metadata for the file
   */
  public DefaultTrackFile(File file, Track track) {
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
  public DefaultTrackFile(File file, int discNum, int trackNum) {
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

  /** {@inheritDoc} */
  @Override
  public int getDiscNum() {
    if (track == null) {
      return discNum;
    } else {
      return track.getDiscNum();
    }
  }

  /** {@inheritDoc} */
  @Override
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
