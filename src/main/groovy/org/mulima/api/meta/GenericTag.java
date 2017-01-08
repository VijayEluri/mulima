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

import org.mulima.util.StringUtil;

/**
 * Contains generic tags that should be common across all tagging systems. Other <code>Tag</code>
 * implementations will tie their values back to values from this class.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public enum GenericTag implements Tag {
  ALBUM,
  ALBUM_SORT,
  ORGANIZATION,
  PUBLISHER,
  PRODUCT_NUMBER,
  CATALOG_NUMBER,
  VOLUME,
  RELEASE_DATE,

  ARTIST,
  ARTIST_SORT,
  GUEST_ARTIST,
  COMPOSER,
  LYRICIST,
  PERFORMER,
  PRODUCER,
  ENGINEER,
  MIXER,
  CONDUCTOR,
  ENSEMBLE,

  DISC_NUMBER,
  TRACK_NUMBER,
  TITLE,
  TITLE_SORT,
  PART,
  VERSION,
  OPUS,
  GENRE,
  DATE,
  LOCATION,
  LYRICS,
  DESCRIPTION,

  CDDB_ID,
  FILE;

  /**
   * Gets itself.
   *
   * @return this
   */
  @Override
  public GenericTag getGeneric() {
    return this;
  }

  /**
   * Gets the camel case form of this tag.
   *
   * @return camel case form of tag
   */
  public String camelCase() {
    return StringUtil.toCamelCase(this.name());
  }

  /**
   * Returns the <code>GenericTag</code> that has the same camel case value as <code>arg0</code>.
   *
   * @param arg0 the camel case name of the constant to return
   * @return the <code>GenericTag</code> with the specified name
   */
  public static GenericTag valueOfCamelCase(String arg0) {
    String name = StringUtil.fromCamelCase(arg0);
    return GenericTag.valueOf(name);
  }
}
