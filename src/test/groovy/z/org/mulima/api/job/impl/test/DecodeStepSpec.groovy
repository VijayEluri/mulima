package z.org.mulima.api.job.impl.test

import spock.lang.Specification
import z.org.mulima.api.audio.Codec
import z.org.mulima.api.audio.CodecResult
import z.org.mulima.api.file.AudioFormat
import z.org.mulima.api.file.impl.DefaultDiscFile
import z.org.mulima.job.Context
import z.org.mulima.job.impl.DecodeStep

class DecodeStepSpec extends Specification {
	Context context = Mock(Context)
	
	def 'execute succeeds when files decoded successfully'() {
		given:
		def codec = Mock(Codec)
		def result = Mock(CodecResult)
		result.success >> true
		codec.format >> AudioFormat.MP3
		codec.decode(_, _) >> result
		def files = [new DefaultDiscFile(new File('test.mp3'), 1)] as Set
		def step = new DecodeStep(context, codec, files)
		expect:
		step.execute()
	}
	
	def 'execute fails when a decode fails'() {
		given:
		def codec = Mock(Codec)
		def result = Mock(CodecResult)
		result.success >>> [true, false]
		codec.format >> AudioFormat.MP3
		codec.decode(_, _) >> result
		def files = [new DefaultDiscFile(new File('test.mp3'), 1), new DefaultDiscFile(new File('test2.mp3'), 1), new DefaultDiscFile(new File('test3.mp3'), 1)] as Set
		def step = new DecodeStep(context, codec, files)
		expect:
		!step.execute()
	}
}
