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
package org.mulima.util;

import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Metadata;
import org.mulima.api.meta.Tag;

/**
 * Helper methods for Metadata operations.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public final class MetadataUtil {
  /**
   * This class should never be instantiated.
   *
   * @throws AssertionError always
   */
  private MetadataUtil() {
    throw new AssertionError("Cannot instantiate this class.");
  }

  /**
   * Calculates the Levenshtein distance between a <code>Tag</code> on two <code>Metadata</code>
   * objects.
   *
   * @param orig the original metadata
   * @param cand the metadata to compare against
   * @param tag the tag to compare
   * @return Levenshtein distance between the two
   */
  public static int tagDistance(Metadata orig, Metadata cand, Tag tag) {
    String origTag = orig.getFlat(tag);
    String candTag = cand.getFlat(tag);
    //		if (origTag.length() < candTag.length()) {
    //			candTag = candTag.substring(0, origTag.length());
    //		}
    return StringUtil.levenshteinDistance(origTag, candTag);
  }

  /**
   * Calculates the Levenshtein distance between two <code>Metadata</code> objects. Uses {@link
   * GenericTag#ARTIST} and {@link GenericTag#ALBUM} to determine the distance.
   *
   * @param orig original metadata
   * @param cand candidate metadata
   * @return Levenshtein distance
   */
  public static int discDistance(Metadata orig, Metadata cand) {
    return tagDistance(orig, cand, GenericTag.ARTIST) + tagDistance(orig, cand, GenericTag.ALBUM);
  }

  /**
   * Finds the common string of the flat values for {@code tag} on each object in {@code metas}.
   *
   * @param metas the metadata objects to search
   * @param tag the tag to search on
   * @return the common string to find
   */
  public static String commonValueFlat(Iterable<? extends Metadata> metas, Tag tag) {
    String value = null;
    for (Metadata meta : metas) {
      if (meta.isSet(tag)) {
        String temp = meta.getFlat(tag);
        value = value == null ? temp : StringUtil.commonString(value, temp);
      }
    }
    return value;
  }
}
