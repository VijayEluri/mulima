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

import org.mulima.api.meta.Album
import org.mulima.api.meta.Disc
import org.mulima.api.meta.GenericTag
import org.mulima.api.meta.Track

abstract class AlbumSpec extends MetadataSpec<Album> {
  abstract void addDisc(Album album, Disc disc)
  abstract void addTrack(Disc disc, Track track)

  def 'getDisc picks the disc by number'() {
    given:
    addDisc(meta, createDisc(1))
    addDisc(meta, createDisc(2))
    addDisc(meta, createDisc(3))
    expect:
    meta.getDisc(2).num == 2
    meta.getDisc(3).num == 3
  }

  Disc createDisc(int num) {
    return factory.fromStringString([DISC_NUMBER:Integer.toString(num)], Disc.class)
  }
}
