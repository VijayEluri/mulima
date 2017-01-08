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

import org.mulima.internal.file.StoredDigestEntry

import spock.lang.Specification

class StoredDigestEntrySpec extends Specification {
  String fileName
  long lastModified
  long size
  String digest
  String notation

  def setup() {
    fileName = 'temp'
    lastModified = 123456
    size = 1000
    digest = 'kajhakjewhfoawiejf'
    notation = "${lastModified},${size},${digest}"
  }

  def 'getModified returens file\'s last modified date'() {
    expect:
    new StoredDigestEntry(fileName, lastModified, size, digest).modified == lastModified
    new StoredDigestEntry(fileName, notation).modified == lastModified
  }

  def 'getSize returns file\'s length'() {
    expect:
    new StoredDigestEntry(fileName, lastModified, size, digest).size == size
    new StoredDigestEntry(fileName, notation).size == size
  }

  def 'getDigest returns digest of file contents'() {
    expect:
    new StoredDigestEntry(fileName, lastModified, size, digest).digest == digest
    new StoredDigestEntry(fileName, notation).digest == digest
  }
}
