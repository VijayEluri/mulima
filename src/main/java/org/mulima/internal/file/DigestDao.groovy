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
package org.mulima.internal.file

import org.mulima.api.file.Digest
import org.mulima.api.file.FileComposer
import org.mulima.api.file.FileParser

/**
 * A DAO that parses and composes digest objects
 * to files.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
class DigestDao implements FileParser<Digest>, FileComposer<Digest> {
  private static final String ID_KEY = 'id'

  /**
   * Parses the specified digest file.
   * @param file the file to parse
   * @return a digest representing the
   * file contents
   */
  Digest parse(File file) {
    Properties props = new Properties()
    file.withInputStream { it ->
      props.load(it)
    }

    UUID id = null
    Set entries = [] as Set
    props.each { key, value ->
      if (ID_KEY == key) {
        id = UUID.fromString(value)
      } else {
        entries << new StoredDigestEntry(key, value)
      }
    }
    return new LazyDigest(id, entries)
  }

  /**
   * Composes a digest object to a file.
   * @param file the file to compose to
   * @param digest the digest to compose
   */
  void compose(File file, Digest digest) {
    if (digest == null) {
      throw new IllegalArgumentException('Digest cannot be null.')
    } else if (digest.id == null) {
      throw new IllegalArgumentException('Digest ID cannot be null.')
    }

    Properties props = new Properties()
    props[ID_KEY] = digest.id.toString()
    digest.entries.each { entry ->
      props[entry.file.name] = entry.toString()
    }
    file.withPrintWriter { writer ->
      props.store(writer, null)
    }
  }
}
