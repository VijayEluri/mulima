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

import java.util.SortedSet;

/**
 * An object that represents a cue sheet for a disc.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface CueSheet extends Metadata, Comparable<CueSheet> {
  /**
   * Gets the disc number of this cue sheet.
   *
   * @return the disc number
   */
  int getNum();

  /**
   * Gets all cue points that correspond to a track's start. (i.e. all points with index 1)
   *
   * @return all index 1 points
   */
  SortedSet<CuePoint> getCuePoints();

  /**
   * Gets all cue points for this sheet.
   *
   * @return all cue points
   */
  SortedSet<CuePoint> getAllCuePoints();
}
