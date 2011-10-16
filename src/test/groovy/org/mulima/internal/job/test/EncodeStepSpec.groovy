package org.mulima.internal.job.test

import org.mulima.api.audio.AudioFormat
import org.mulima.api.audio.action.Codec
import org.mulima.api.audio.action.CodecResult
import org.mulima.api.audio.file.AudioFile
import org.mulima.api.audio.file.AudioFileFactory
import org.mulima.api.file.TempDir
import org.mulima.api.service.MulimaService
import org.mulima.internal.audio.file.DefaultDiscFile
import org.mulima.internal.job.EncodeStep

import spock.lang.Specification

class EncodeStepSpec extends Specification {
	MulimaService service = Mock(MulimaService)
	
	def setup() {
		TempDir temp = Mock(TempDir)
		TempDir temp2 = Mock(TempDir)
		service.tempDir >> temp
		temp.newChild() >> temp2
		AudioFileFactory factory = Mock(AudioFileFactory)
		service.audioFileFactory >> factory
		factory.createAudioFile(_, _, _) >> Mock(AudioFile)
	}
	
	def 'execute succeeds when files encoded successfully'() {
		given:
		def codec = Mock(Codec)
		service.getCodec(_) >> codec
		def result = Mock(CodecResult)
		result.success >> true
		codec.format >> AudioFormat.MP3
		codec.encode(_, _) >> result
		def files = [new DefaultDiscFile(new File('test.wav'), 1)] as Set
		def step = new EncodeStep(service, AudioFormat.MP3, files)
		expect:
		step.execute()
	}
	
	def 'execute fails when an encode fails'() {
		given:
		def codec = Mock(Codec)
		service.getCodec(_) >> codec
		def result = Mock(CodecResult)
		result.success >>> [true, false]
		codec.format >> AudioFormat.MP3
		codec.encode(_, _) >> result
		def files = [new DefaultDiscFile(new File('test.wav'), 1), new DefaultDiscFile(new File('test2.wav'), 1), new DefaultDiscFile(new File('test3.wav'), 1)] as Set
		def step = new EncodeStep(service, AudioFormat.MP3, files)
		expect:
		!step.execute()
	}
}
