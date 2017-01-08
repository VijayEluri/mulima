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

import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Tag;

/**
 * Set of tags used for files using ID3v2 tags.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public enum ID3V2Tag implements Tag {
  TALB(GenericTag.ALBUM),
  TSOA(GenericTag.ALBUM_SORT),
  TPUB(GenericTag.ORGANIZATION),
  TDOR(GenericTag.RELEASE_DATE),

  TPE1(GenericTag.ARTIST),
  TDOP(GenericTag.ARTIST_SORT),
  TCOM(GenericTag.COMPOSER),
  TEXT(GenericTag.LYRICIST),
  TMLC(GenericTag.PERFORMER),
  TIPL_producer(GenericTag.PRODUCER),
  TIPL_engineer(GenericTag.ENGINEER),
  TIPL_mix(GenericTag.MIXER),
  TPE3(GenericTag.CONDUCTOR),

  TPOS(GenericTag.DISC_NUMBER),
  TRCK(GenericTag.TRACK_NUMBER),
  TIT2(GenericTag.TITLE),
  TSOT(GenericTag.TITLE_SORT),
  TCON(GenericTag.GENRE),
  TDRC(GenericTag.DATE),
  LOCATION(GenericTag.LOCATION),
  USLT(GenericTag.LYRICS),
  COMM(GenericTag.DESCRIPTION);

  private final GenericTag tag;

  /**
   * Constructs a tag.
   *
   * @param tag the generic tag this maps to
   */
  private ID3V2Tag(GenericTag tag) {
    this.tag = tag;
  }

  /** {@inheritDoc} */
  @Override
  public GenericTag getGeneric() {
    return tag;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.name().replaceAll("_", ":");
  }

  /**
   * Conversion method for getting the matching <code>ID3V2Tag</code> for a given <code>GenericTag
   * </code>.
   *
   * @param generic the generic tag to look for
   * @return the ID3v2 tag that corresponds to <code>generic</code>.
   */
  public static ID3V2Tag valueOf(GenericTag generic) {
    for (ID3V2Tag tag : ID3V2Tag.values()) {
      if (generic.equals(tag.getGeneric())) {
        return tag;
      }
    }
    return null;
  }
}
