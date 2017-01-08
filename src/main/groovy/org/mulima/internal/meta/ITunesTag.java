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
 * Set of tags used for iTunes AAC files.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public enum ITunesTag implements Tag {
  album(GenericTag.ALBUM),
  sortalbum(GenericTag.ALBUM_SORT),
  label(GenericTag.ORGANIZATION),

  artist(GenericTag.ARTIST),
  sortartist(GenericTag.ARTIST_SORT),
  composer(GenericTag.COMPOSER),

  disc(GenericTag.DISC_NUMBER),
  track(GenericTag.TRACK_NUMBER),
  title(GenericTag.TITLE),
  sorttitle(GenericTag.TITLE_SORT),
  genre(GenericTag.GENRE),
  year(GenericTag.DATE),
  lyrics(GenericTag.LYRICS),
  comment(GenericTag.DESCRIPTION);

  private final GenericTag tag;

  /**
   * Constructs a tag.
   *
   * @param tag the generic tag this maps to
   */
  private ITunesTag(GenericTag tag) {
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
   * Conversion method for getting the matching <code>ITunesTag</code> for a given <code>GenericTag
   * </code>.
   *
   * @param generic the generic tag to look for
   * @return the iTunes tag that corresponds to <code>generic</code>.
   */
  public static ITunesTag valueOf(GenericTag generic) {
    for (ITunesTag tag : ITunesTag.values()) {
      if (generic.equals(tag.getGeneric())) {
        return tag;
      }
    }
    return null;
  }
}
