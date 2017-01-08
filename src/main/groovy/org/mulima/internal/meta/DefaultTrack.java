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

import org.mulima.api.meta.CuePoint;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Track;

/**
 * Default implementation of a track.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DefaultTrack extends AbstractMetadata implements Track {
  private CuePoint startPoint = null;
  private CuePoint endPoint = null;

  public DefaultTrack() {
    super(null);
  }

  public DefaultTrack(Disc disc) {
    super(disc);
  }

  /** {@inheritDoc} */
  @Override
  public int getNum() {
    return Integer.valueOf(getFirst(GenericTag.TRACK_NUMBER));
  }

  /** {@inheritDoc} */
  @Override
  public int getDiscNum() {
    try {
      return Integer.valueOf(getFirst(GenericTag.DISC_NUMBER));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /** {@inheritDoc} */
  @Override
  public CuePoint getStartPoint() {
    return startPoint;
  }

  /** {@inheritDoc} */
  @Override
  public void setStartPoint(CuePoint startPoint) {
    this.startPoint = startPoint;
  }

  /** {@inheritDoc} */
  @Override
  public CuePoint getEndPoint() {
    return endPoint;
  }

  /** {@inheritDoc} */
  @Override
  public void setEndPoint(CuePoint endPoint) {
    this.endPoint = endPoint;
  }

  /** {@inheritDoc} */
  @Override
  public void tidy() {
    //no-op
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
    } else if (obj instanceof DefaultTrack) {
      DefaultTrack that = (DefaultTrack) obj;
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
