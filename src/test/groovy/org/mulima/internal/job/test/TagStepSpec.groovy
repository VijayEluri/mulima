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
package org.mulima.internal.job.test

import org.mulima.api.audio.tool.Codec
import org.mulima.api.audio.tool.CodecResult
import org.mulima.api.audio.tool.Tagger
import org.mulima.api.audio.tool.TaggerResult
import org.mulima.api.audio.tool.ToolService
import org.mulima.api.file.FileService
import org.mulima.api.file.TempDir
import org.mulima.api.file.audio.AudioFile
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.service.MulimaService
import org.mulima.internal.file.audio.DefaultDiscFile
import org.mulima.internal.job.DecodeStep
import org.mulima.internal.job.TagStep

import spock.lang.Specification

class TagStepSpec extends Specification {
  MulimaService service = Mock(MulimaService)

  def setup() {
    TempDir temp = Mock(TempDir)
    TempDir temp2 = Mock(TempDir)
    service.tempDir >> temp
    temp.newChild() >> temp2
    FileService fileService = Mock(FileService)
    service.fileService >> fileService
    fileService.createAudioFile(_, _, _) >> Mock(AudioFile)
  }

  def 'execute succeeds when files tag successfully'() {
    given:
    ToolService toolService = Mock(ToolService)
    service.toolService >> toolService
    Tagger tagger = Mock(Tagger)
    tagger.format >> AudioFormat.MP3
    toolService.getTagger(_) >> tagger
    def result = Mock(TaggerResult)
    result.success >> true
    def files = [new DefaultDiscFile(new File('test.mp3'), 1), new DefaultDiscFile(new File('test2.mp3'), 1)] as Set
    when:
    def success = new TagStep(service, files).execute()
    then:
    success
    interaction {
      files.each {
        1*tagger.write(it) >> result
      }
    }
  }

  def 'execute fails when a tag fails'() {
    given:
    ToolService toolService = Mock(ToolService)
    service.toolService >> toolService
    Tagger tagger = Mock(Tagger)
    tagger.format >> AudioFormat.MP3
    toolService.getTagger(_) >> tagger
    def result = Mock(TaggerResult)
    result.success >>> [true, false]
    tagger.write(_) >> result
    def files = [new DefaultDiscFile(new File('test.mp3'), 1), new DefaultDiscFile(new File('test2.mp3'), 1), new DefaultDiscFile(new File('test3.mp3'), 1)] as Set
    expect:
    !new TagStep(service, files).execute()
  }
}
