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
