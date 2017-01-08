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

import org.mulima.internal.file.LeafDirFilter

import spock.lang.Specification

class LeafDirFilterSpec extends Specification {
  def 'accept returns true for directories with no subdirs'() {
    expect:
    new LeafDirFilter().accept(mockFile(true, mockFile(false)))
    new LeafDirFilter().accept(mockFile(true, mockFile(false), mockFile(false)))
  }

  def 'accept returns false for empty directories'() {
    expect:
    !new LeafDirFilter().accept(mockFile(true))
  }

  def 'accept returns false for directories with subdirs'() {
    expect:
    !new LeafDirFilter().accept(mockFile(true, mockFile(false), mockFile(false), mockFile(true)))
    !new LeafDirFilter().accept(mockFile(true, mockFile(true)))
  }

  def 'accept returns false for files'() {
    expect:
    !new LeafDirFilter().accept(mockFile(false))
  }

  def mockFile(boolean isDirectory, File... children) {
    File file = Mock()
    file.directory >> isDirectory
    if (isDirectory && children != null) {
      file.listFiles() >> (children as File[])
    }
    file.absolutePath >> '/opt/music/blah.flac'
    return file
  }
}
