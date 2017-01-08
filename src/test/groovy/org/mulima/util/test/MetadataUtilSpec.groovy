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
package org.mulima.util.test

import org.mulima.api.meta.GenericTag
import org.mulima.api.meta.Metadata
import org.mulima.api.meta.test.MetadataFactory
import org.mulima.internal.meta.DefaultDisc
import org.mulima.internal.meta.DefaultTrack
import org.mulima.util.MetadataUtil

import spock.lang.Shared
import spock.lang.Specification

class MetadataUtilSpec extends Specification {
  @Shared Metadata meta1
  @Shared Metadata meta2
  @Shared Metadata meta3

  def setupSpec() {
    meta1 = new DefaultDisc()
    meta1.add(GenericTag.ARTIST, 'Genesis')
    meta1.add(GenericTag.ALBUM, 'The Lamb Lies Down On Broadway (Disc 1)')
    meta1.add(GenericTag.DISC_NUMBER, '1')

    meta2 = new DefaultDisc()
    meta2.add(GenericTag.ARTIST, 'Genesis')
    meta2.add(GenericTag.ALBUM, 'The Lamb Lies Down On Broadway (Disc 2)')
    meta2.add(GenericTag.DISC_NUMBER, '2')

    meta3 = new DefaultDisc()
    meta3.add(GenericTag.ARTIST, 'Regenesis')
    meta3.add(GenericTag.ALBUM, 'A Tribute To The Lamb Lies Down On Broadway')
    meta3.add(GenericTag.DISC_NUMBER, '1')
  }

  def 'tagDistance shows levenshtein distance on a specific tag'() {
    expect:
    MetadataUtil.tagDistance(obj1, obj2, tag) == distance
    where:
    obj1	| obj2	| tag						| distance
    meta1	| meta2	| GenericTag.ARTIST			| 0
    meta1	| meta2	| GenericTag.ALBUM			| 1
    meta1	| meta2	| GenericTag.DISC_NUMBER	| 1
    meta2	| meta3	| GenericTag.ARTIST			| 3
    meta2	| meta3	| GenericTag.ALBUM			| 22
  }

  def 'discDistance shows levenshtein distance of ARTIST and ALBUM tags'() {
    expect:
    MetadataUtil.discDistance(obj1, obj2) == distance
    where:
    obj1	| obj2	| distance
    meta1	| meta2	| 1
    meta2	| meta3	| 25
  }

  def 'commonValueFlat returns the common value for the tag'() {
    given:
    MetadataFactory factory = new MetadataFactory()
    factory.registerImplementation(Metadata, DefaultTrack)
    Metadata meta1 = factory.fromStringString([ALBUM:'The Sane Day (Disc 1)'], Metadata)
    Metadata meta2 = factory.fromStringString([ALBUM:'The Sane Day (Disc 2)'], Metadata)
    List metas = [meta1, meta2]
    expect:
    MetadataUtil.commonValueFlat(metas, GenericTag.ALBUM) == 'The Sane Day (Disc '
  }
}
