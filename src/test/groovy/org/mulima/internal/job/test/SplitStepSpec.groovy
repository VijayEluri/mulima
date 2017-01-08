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

import org.mulima.api.audio.tool.Splitter
import org.mulima.api.audio.tool.SplitterResult
import org.mulima.api.audio.tool.ToolService
import org.mulima.api.file.FileService
import org.mulima.api.file.TempDir
import org.mulima.api.file.audio.AudioFile
import org.mulima.api.file.audio.DiscFile
import org.mulima.api.file.audio.TrackFile
import org.mulima.api.meta.Disc
import org.mulima.api.meta.Track
import org.mulima.api.service.MulimaService
import org.mulima.internal.file.audio.DefaultDiscFile
import org.mulima.internal.job.SplitStep

import spock.lang.Specification

class SplitStepSpec extends Specification {
  MulimaService service = Mock(MulimaService)

  def setup() {
    service.tempDir >> new TempDir().newChild('mulimaTest')
    FileService fileService = Mock(FileService)
    service.fileService >> fileService
    fileService.createAudioFile(_, _, _) >> Mock(AudioFile)
    fileService.createTrackFile(_) >> Mock(TrackFile)
  }

  def 'execute succeeds when files split successfully'() {
    given:
    ToolService toolService = Mock(ToolService)
    service.toolService >> toolService
    Splitter splitter = Mock(Splitter)
    toolService.splitter >> splitter
    def dests = [Mock(TrackFile), Mock(TrackFile), Mock(TrackFile), Mock(TrackFile)] as Set
    SplitterResult result = new SplitterResult(null, dests, '', 0, '', '')
    def files = [mockDisc('test.wav'), mockDisc('test2.wav'), mockDisc('test3.wav')] as Set
    files.each {
      it.meta = Mock(Disc)
      it.meta.tracks >> ([Mock(Track)] as SortedSet)
    }
    when:
    def success = new SplitStep(service, files, service.tempDir.newChild().file).execute()
    then:
    success
    interaction {
      files.each {
        1*splitter.split(it, _) >> result
      }
    }
  }

  def 'execute fails when a split fails'() {
    given:
    ToolService toolService = Mock(ToolService)
    service.toolService >> toolService
    Splitter splitter = Mock(Splitter)
    toolService.splitter >> splitter
    def dests = [Mock(TrackFile)] as Set
    SplitterResult success = new SplitterResult(null, dests, '', 0, '', '')
    SplitterResult failure = new SplitterResult(null, null, '', 1, '', '')
    splitter.split(_, _) >>> [success, failure]
    def files = [mockDisc('test.wav'), mockDisc('test2.wav'), mockDisc('test3.wav')] as Set
    files.each {
      it.meta = Mock(Disc)
      it.meta.tracks >> ([Mock(Track)] as SortedSet)
    }
    expect:
    !new SplitStep(service, files, service.tempDir.newChild().file).execute()
  }

  DiscFile mockDisc(String name) {
    Disc meta = Mock()
    meta.tracks >> ([mockTrack(), mockTrack()] as SortedSet)
    DiscFile file = Mock()
    file.file >> new File(name)
    file.meta >> meta
    return file
  }

  Track mockTrack() {
    Track track = Mock()
    track.compareTo(_) >> { track.is(it) ? 0 : 1 }
    return track
  }
}
