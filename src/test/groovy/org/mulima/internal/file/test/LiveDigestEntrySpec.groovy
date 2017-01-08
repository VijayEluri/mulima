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

import org.mulima.internal.file.LiveDigestEntry

import spock.lang.Specification

class LiveDigestEntrySpec extends Specification {
  File file
  long lastModified
  long size
  String digest

  def setup() {
    file = File.createTempFile('digest', '.txt')
    file.withPrintWriter { writer ->
      writer.println 'These are the contents of the file.'
    }
    lastModified = file.lastModified()
    size = file.length()
    if (System.properties['line.separator'] == '\n') {
      digest = '8786b174d5e3a9edd290a7418c800018c9087768'
    } else {
      digest = '43d20799f81b8e5b4a85febdffb07eb2a59f84c6'
    }
  }

  def cleanup() {
    assert file.delete()
  }

  def 'getModified returens file\'s last modified date'() {
    expect:
    new LiveDigestEntry(file).modified == lastModified
  }

  def 'getSize returns file\'s length'() {
    expect:
    new LiveDigestEntry(file).size == size
  }

  def 'getDigest returns digest of file contents'() {
    expect:
    new LiveDigestEntry(file).digest == digest
  }
}
