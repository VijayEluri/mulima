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
package org.mulima.internal.file.test

import org.mulima.internal.file.LazyDigest
import org.mulima.internal.file.StoredDigestEntry

import spock.lang.Specification

class LazyDigestSpec extends Specification {
  def 'equals returns true if ids are the same and digests are lazily equal'() {
    given:
    UUID id = UUID.randomUUID()
    String fileName1 = 'temp1'
    String fileName2 = 'temp2'
    def entries = [new StoredDigestEntry(fileName1, 0, 0, ''), new StoredDigestEntry(fileName2, 1000, 2000, 'the')] as Set
    def entries2 = [new StoredDigestEntry(fileName1, 0, 0, ''), new StoredDigestEntry(fileName2, 1000, 2000, 'te')] as Set
    def entries3 = [new StoredDigestEntry(fileName1, 0, 1000, ''), new StoredDigestEntry(fileName2, 1000, 2000, 'the')] as Set
    def entries4 = [new StoredDigestEntry(fileName1, 1000, 0, 'a'), new StoredDigestEntry(fileName2, 1000, 2000, 'the')] as Set
    expect:
    new LazyDigest(id, entries) == new LazyDigest(id, entries)
    new LazyDigest(id, entries2) == new LazyDigest(id, entries2)
    new LazyDigest(id, entries3) == new LazyDigest(id, entries3)
    new LazyDigest(id, entries4) == new LazyDigest(id, entries4)
    new LazyDigest(id, entries) == new LazyDigest(id, entries2)
    new LazyDigest(id, entries) == new LazyDigest(id, entries3)
    new LazyDigest(id, entries) != new LazyDigest(id, entries4)
  }
}
