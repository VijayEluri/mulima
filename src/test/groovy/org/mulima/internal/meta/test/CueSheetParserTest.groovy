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
import org.mulima.api.meta.CueSheet
import org.mulima.internal.meta.CueSheetParser

class CueSheetParserTest {
  private CueSheet exampleCue
  private File exampleFile
  private File tempFile

  @Before
  void prepareCues() {
    exampleCue = CueSheetHelper.exampleCue
    exampleFile = File.createTempFile('example', '.cue')
    tempFile = File.createTempFile('temp', '.cue')
    CueSheetHelper.writeExampleFile(exampleFile)
  }

  @Test
  void parse() {
    def cue = new CueSheetParser().parse(exampleFile)
    assert exampleCue == cue
  }

  @After
  void cleanup() {
    exampleFile.deleteOnExit()
    tempFile.deleteOnExit()
  }
}
