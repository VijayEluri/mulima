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
