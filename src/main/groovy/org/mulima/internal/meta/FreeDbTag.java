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
 * Set of tags used by FreeDB.
 *
 * <p>For more information see their website {@link http://www.freedb.org/}.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public enum FreeDbTag implements Tag {
  DISCID(GenericTag.CDDB_ID),
  DARTIST(GenericTag.ARTIST),
  DTITLE(GenericTag.ALBUM),
  DYEAR(GenericTag.DATE),
  DGENRE(GenericTag.GENRE),
  TTITLE(GenericTag.TITLE),
  TRACK_NUM(GenericTag.TRACK_NUMBER);

  private final GenericTag tag;

  /**
   * Constructs a tag.
   *
   * @param tag the generic tag this maps to
   */
  private FreeDbTag(GenericTag tag) {
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
    return this.name().replaceAll("_", " ");
  }
}
