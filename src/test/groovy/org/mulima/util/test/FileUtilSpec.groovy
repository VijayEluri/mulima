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
package org.mulima.util.test

import org.mulima.util.FileUtil

import spock.lang.Specification

class FileUtilSpec extends Specification {
  def 'changeExtension returns same file path with only extension changed'() {
    given:
    File file = new File("Temp${File.separator}testPotatoe.txt")
    expect:
    FileUtil.changeExtension(file, 'flac') == new File("Temp${File.separator}testPotatoe.flac")
  }

  def 'getBaseName returns only the non-extension portion of the file name'() {
    given:
    File file = new File("Temp${File.separator}testPotatoe.txt")
    expect:
    FileUtil.getBaseName(file) == 'testPotatoe'
  }

  def 'getSafeCanonicalPath returns same value as getCanonicalPath if it worked'() {
    given:
    File file = Mock(File)
    String path = '/etc/apt/sources.list'
    file.canonicalPath >> path
    expect:
    FileUtil.getSafeCanonicalPath(file) == path
  }

  def 'getSafeCanonicalPath returns null if getCanonicalPath fails'() {
    given:
    File file = Mock(File)
    file.canonicalPath >> { throw new IOException() }
    expect:
    FileUtil.getSafeCanonicalPath(file) == null
  }

  def 'listDirsRecursive returns list of all subdirectories of the parameter'() {
    given:
    File dir4 = mockFile(true, [mockFile(false, null), mockFile(false, null)])
    File dir3 = mockFile(true, [mockFile(false, null), dir4, mockFile(false, null)])
    File dir2 = mockFile(true, [mockFile(false, null)])
    File dir1 = mockFile(true, [mockFile(false, null), dir2, dir3])
    expect:
    FileUtil.listDirsRecursive(dir1) == [dir1, dir2, dir3, dir4]
  }

  def 'listDirsRecursive fails if a non-directory is passed in'() {
    given:
    File file = Mock(File)
    file.directory >> false
    when:
    FileUtil.listDirsRecursive(file)
    then:
    thrown(IllegalArgumentException)
  }

  def mockFile(boolean isDirectory, List children) {
    File file = Mock(File)
    file.directory >> isDirectory
    if (isDirectory && children != null) {
      file.listFiles() >> (children as File[])
    }
    return file
  }
}
