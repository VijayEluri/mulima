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

import java.util.List;
import java.util.Map;

/**
 * Represents an object that holds metadata. Implementations should support multiple values per tag.
 *
 * @author Andrew Oberstar
 * @see Tag
 * @since 0.1.0
 */
public interface Metadata {
  /**
   * Gets the parent metadata object.
   *
   * @return the parent, or {@code null} if there isn't one
   */
  Metadata getParent();

  /**
   * Checks if the given tag is set on this metadata. If the tag is null the method will return
   * false.
   *
   * @param tag the tag to check.
   * @return <code>true</code> if it is set, <code>false</code> otherwise
   */
  boolean isSet(Tag tag);

  /**
   * Adds the specified value for the specified tag. If the the tag or value is null the method will
   * return without taking any action.
   *
   * @param tag the tag to add <code>value</code> for
   * @param value the value to add to <code>tag</code>
   */
  void add(Tag tag, String value);

  /**
   * Adds all of the specified values for the specified tag. If the tag or list is null the method
   * will return without taking any action.
   *
   * @param tag the tag to add <code>values</code> for
   * @param values the values to add to <code>tag</code>
   */
  void addAll(Tag tag, List<String> values);

  /**
   * Adds all of the metadata from the given object to this instance.
   *
   * @param meta a Metadata object with tags (and values) to be added to this one
   */
  void addAll(Metadata meta);

  /**
   * Gets all values for the given tag. If there are no values for the tag an empty list will be
   * returned. If the tag is null, an empty list will be returned.
   *
   * @param tag the tag to get values for
   * @return the values of <code>tag</code>
   */
  List<String> getAll(Tag tag);

  /**
   * Gets the first value for a given tag. If the tag is null, the method will return null.
   *
   * @param tag the tag to get the value of
   * @return the first value for <code>tag</code>
   */
  String getFirst(Tag tag);

  /**
   * Gets all values for a tag in a single <code>String</code>. If the tag is null, this method will
   * return null.
   *
   * @param tag the tag to get the values of
   * @return all values of <code>tag</code> in <code>String</code> format
   */
  String getFlat(Tag tag);

  /**
   * Gets a <code>Map</code> of the values backing this object. If no tags are set, this method will
   * return an empty map.
   *
   * @return map of values
   */
  Map<GenericTag, List<String>> getMap();

  /**
   * Removes all values for a tag. If the tag is null, the method will return with no action taken.
   *
   * @param tag that tag to remove
   */
  void remove(Tag tag);

  /** Removes all tags and values. */
  void removeAll();

  /** Simplify the metadata, by moving all values common among all children to their parent. */
  void tidy();
}
