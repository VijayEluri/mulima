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
package org.mulima.api.meta.test

import org.mulima.api.meta.GenericTag
import org.mulima.api.meta.Track

abstract class TrackSpec extends MetadataSpec<Track> {
  def 'getNum gets value from tag value'() {
    given:
    meta.add(GenericTag.TRACK_NUMBER, Integer.toString(num))
    expect:
    meta.num == num
    where:
    num << [1, 2, 3]
  }

  def 'tracks sort by disc num first'() {
    given:
    Track track1 = factory.newInstance(Track)
    track1.add(GenericTag.DISC_NUMBER, Integer.toString(discNum1))
    track1.add(GenericTag.TRACK_NUMBER, Integer.toString(trackNum1))
    and:
    Track track2 = factory.newInstance(Track)
    track2.add(GenericTag.DISC_NUMBER, Integer.toString(discNum2))
    track2.add(GenericTag.TRACK_NUMBER, Integer.toString(trackNum2))
    expect:
    track1 > track2
    where:
    discNum1 | trackNum1 | discNum2 | trackNum2
    2 | 1 | 1 | 2
  }
}
