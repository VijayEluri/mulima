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
package org.mulima.internal.meta.test

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mulima.api.meta.Album
import org.mulima.internal.meta.AlbumXmlDao

class AlbumXmlDaoTest {
  private Album exampleAlbum
  private File exampleXml
  private File tempXml

  @Before
  void prepareAlbums() {
    exampleAlbum = AlbumXmlHelper.exampleAlbum
    exampleXml = File.createTempFile('example', '.xml')
    tempXml = File.createTempFile('temp', '.xml')
    AlbumXmlHelper.writeExampleFile(exampleXml)
  }

  @Test
  void parse() {
    def album = new AlbumXmlDao().parse(exampleXml)
    assert exampleAlbum == album
  }

  @Test
  void compose() {
    new AlbumXmlDao().compose(tempXml, exampleAlbum)

    def temp = []
    tempXml.eachLine { temp.add(it.trim()) }

    def example = []
    exampleXml.eachLine { example.add(it.trim()) }

    assert example == temp
  }

  @After
  void cleanup() {
    exampleXml?.deleteOnExit()
    tempXml?.deleteOnExit()
  }
}
