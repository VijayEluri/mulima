package org.mulima.internal.job.test

import org.mulima.api.audio.tool.Splitter
import org.mulima.api.audio.tool.SplitterResult
import org.mulima.api.audio.tool.ToolService
import org.mulima.api.file.FileService
import org.mulima.api.file.TempDir
import org.mulima.api.file.audio.AudioFile
import org.mulima.api.file.audio.TrackFile
import org.mulima.api.service.MulimaService
import org.mulima.internal.file.audio.DefaultDiscFile
import org.mulima.internal.job.SplitStep

import spock.lang.Specification

class SplitStepSpec extends Specification {
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
	
	def 'execute succeeds when files split successfully'() {
		given:
		ToolService toolService = Mock(ToolService)
		service.toolService >> toolService
		Splitter splitter = Mock(Splitter)
		toolService.splitter >> splitter
		def dests = [Mock(TrackFile), Mock(TrackFile), Mock(TrackFile), Mock(TrackFile)] as Set
		SplitterResult result = new SplitterResult(null, dests, '', 0, '', '')
		def files = [new DefaultDiscFile(new File('test.wav'), 1), new DefaultDiscFile(new File('test2.wav'), 1)] as Set
		when:
		def success = new SplitStep(service, files).execute()
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
		def files = [new DefaultDiscFile(new File('test.wav'), 1), new DefaultDiscFile(new File('test2.wav'), 1), new DefaultDiscFile(new File('test3.wav'), 1)] as Set
		expect:
		!new SplitStep(service, files).execute()
	}
}
