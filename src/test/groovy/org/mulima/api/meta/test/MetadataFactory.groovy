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
package org.mulima.api.meta.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Metadata;
import org.mulima.api.meta.Tag;

/**
 * Provides factory methods to create <code>Metadata</code>
 * objects from various forms of <code>Map</code>s.
 * @see Metadata
 */
final class MetadataFactory {
  private final Map implementations = [:]

  Class getImplementation(Class type) {
    return implementations.get(type)
  }

  void registerImplementation(Class type, Class implementation) {
    implementations[type] = implementation;
  }

  Metadata newInstance(Class type, Object... args) throws InstantiationException, IllegalAccessException {
    return getImplementation(type).newInstance(args);
  }

  /**
   * Converts a <code>Map</code> to a <code>TagSupport</code>.
   * @param map the map to convert
   * @return a <code>TagSupport</code> containing the specified values
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  Metadata fromTagList(Map map, Class type, Object... args) throws InstantiationException, IllegalAccessException {
    Metadata meta = getImplementation(type).newInstance(args);
    for (Entry<Tag, List<String>> entry : map.entrySet()) {
      Tag tag = entry.getKey();
      meta.addAll(tag, entry.getValue());
    }
    return meta;
  }

  /**
   * Converts a <code>Map</code> to a <code>TagSupport</code>.
   * @param map the map to convert
   * @return a <code>TagSupport</code> containing the specified values
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public <T extends Metadata> T fromStringList(Map<String, List<String>> map, Class<T> type, Object... args) throws InstantiationException, IllegalAccessException {
    T meta = getImplementation(type).newInstance(args);
    for (Entry<String, List<String>> entry : map.entrySet()) {
      Tag tag = GenericTag.valueOf(entry.getKey());
      meta.addAll(tag, entry.getValue());
    }
    return meta;
  }

  /**
   * Converts a <code>Map</code> to a <code>TagSupport</code>.
   * @param map the map to convert
   * @return a <code>TagSupport</code> containing the specified values
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public <T extends Metadata> T fromTagString(Map<Tag, String> map, Class<T> type, Object... args) throws InstantiationException, IllegalAccessException {
    T meta = getImplementation(type).newInstance(args);
    for (Entry<Tag, String> entry : map.entrySet()) {
      Tag tag = entry.getKey();
      meta.add(tag, entry.getValue());
    }
    return meta;
  }

  /**
   * Converts a <code>Map</code> to a <code>TagSupport</code>.
   * @param map the map to convert
   * @return a <code>TagSupport</code> containing the specified values
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public <T extends Metadata> T fromStringString(Map<String, String> map, Class<T> type, Object... args) throws InstantiationException, IllegalAccessException {
    T meta = getImplementation(type).newInstance(args);
    for (Entry<String, String> entry : map.entrySet()) {
      Tag tag = GenericTag.valueOf(entry.getKey());
      meta.add(tag, entry.getValue());
    }
    return meta;
  }
}
