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
package org.mulima.internal.meta;

import java.util.SortedSet;
import java.util.TreeSet;

import org.mulima.api.meta.Album;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Track;

/**
 * Default implementation of a disc.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DefaultDisc extends AbstractMetadata implements Disc {
  private final SortedSet<Track> tracks = new TreeSet<Track>();

  public DefaultDisc() {
    super(null);
  }

  public DefaultDisc(Album album) {
    super(album);
  }

  /** {@inheritDoc} */
  @Override
  public int getNum() {
    return Integer.valueOf(getFirst(GenericTag.DISC_NUMBER));
  }

  /** {@inheritDoc} */
  @Override
  public SortedSet<Track> getTracks() {
    return tracks;
  }

  /** {@inheritDoc} */
  @Override
  public Track getTrack(int num) {
    for (Track track : tracks) {
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
    } else if (obj instanceof DefaultDisc) {
      DefaultDisc that = (DefaultDisc) obj;
      return this.getMap().equals(that.getMap()) && this.getTracks().equals(that.getTracks());
    } else {
      return false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int result = 23;
    result = result * 31 + getMap().hashCode();
    result = result * 31 + getTracks().hashCode();
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[tags:");
    builder.append(getMap());
    builder.append(", tracks:");
    builder.append(getTracks());
    builder.append("]");
    return builder.toString();
  }
}
