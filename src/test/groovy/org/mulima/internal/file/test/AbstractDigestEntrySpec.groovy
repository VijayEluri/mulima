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

import org.mulima.api.file.DigestEntry
import org.mulima.internal.file.LiveDigestEntry
import org.mulima.internal.file.StoredDigestEntry

import spock.lang.Specification

class AbstractDigestEntrySpec extends Specification {
  DigestEntry live
  DigestEntry store
  DigestEntry storeM
  DigestEntry storeS
  DigestEntry storeD

  def setup() {
    File file = File.createTempFile('digest', '.txt')
    file.withPrintWriter { writer ->
      writer.println 'These are the contents of the file.'
    }
    long lastModified = file.lastModified()
    long size = file.length()
    String digest
    if (System.properties['line.separator'] == '\n') {
      digest = '8786b174d5e3a9edd290a7418c800018c9087768'
    } else {
      digest = '43d20799f81b8e5b4a85febdffb07eb2a59f84c6'
    }
    live = new LiveDigestEntry(file)
    store = new StoredDigestEntry(file.name, lastModified, size, digest)
    storeM = new StoredDigestEntry(file.name, 0, size, digest)
    storeS = new StoredDigestEntry(file.name, lastModified, 0, digest)
    storeD = new StoredDigestEntry(file.name, lastModified, size, '')
  }

  def 'equals returns false only if digest is different'() {
    expect:
    live == live
    store == store
    live == store
    store == live
    store == storeM
    storeM == store
    live == storeM
    storeM == live
    store == storeS
    storeS == store
    live == storeS
    storeS == live
    store != storeD
    storeD != store
    live != storeD
    storeD != live
  }

  def 'lazyEquals returns true if modified and size are the same'() {
    expect:
    live.lazyEquals(live)
    store.lazyEquals(store)
    live.lazyEquals(store)
    store.lazyEquals(live)
    store.lazyEquals(storeD)
    storeD.lazyEquals(store)
    live.lazyEquals(storeD)
    storeD.lazyEquals(live)
  }

  def 'lazyEquals returns true if modifed or size is different but digest is the same'() {
    expect:
    store.lazyEquals(storeM)
    storeM.lazyEquals(store)
    live.lazyEquals(storeM)
    storeM.lazyEquals(live)
    store.lazyEquals(storeS)
    storeS.lazyEquals(store)
    live.lazyEquals(storeS)
    storeS.lazyEquals(live)
  }

  def 'lazyEquals returns dalse if all three attributes are different'() {
    given:
    def storeA = new StoredDigestEntry(null, 0, 0, '')
    expect:
    !store.lazyEquals(storeA)
    !storeA.lazyEquals(store)
  }
}
