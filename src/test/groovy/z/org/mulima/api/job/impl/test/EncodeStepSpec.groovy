package z.org.mulima.api.job.impl.test

import spock.lang.Specification
import z.org.mulima.api.audio.Codec
import z.org.mulima.api.audio.CodecResult
import z.org.mulima.api.file.AudioFormat
import z.org.mulima.api.file.impl.DefaultDiscFile
import z.org.mulima.job.Context
import z.org.mulima.job.impl.EncodeStep

class EncodeStepSpec extends Specification {
	Context context = Mock(Context)
	
	def 'execute succeeds when files encoded successfully'() {
		given:
		def codec = Mock(Codec)
		def result = Mock(CodecResult)
		result.success >> true
		codec.format >> AudioFormat.MP3
		codec.encode(_, _) >> result
		def files = [new DefaultDiscFile(new File('test.wav'), 1)] as Set
		def step = new EncodeStep(context, codec, files)
		expect:
		step.execute()
	}
	
	def 'execute fails when an encode fails'() {
		given:
		def codec = Mock(Codec)
		def result = Mock(CodecResult)
		result.success >>> [true, false]
		codec.format >> AudioFormat.MP3
		codec.encode(_, _) >> result
		def files = [new DefaultDiscFile(new File('test.wav'), 1), new DefaultDiscFile(new File('test2.wav'), 1), new DefaultDiscFile(new File('test3.wav'), 1)] as Set
		def step = new EncodeStep(context, codec, files)
		expect:
		!step.execute()
	}
}
