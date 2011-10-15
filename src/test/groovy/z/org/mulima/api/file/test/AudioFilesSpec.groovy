package z.org.mulima.api.audio.test

import spock.lang.Specification
import z.org.mulima.api.audio.AudioFiles

class AudioFilesSpec extends Specification {
	def 'createDiscFile works with valid file name'() {
		given:
		def file = new File("D${discNum} DiscName.flac")
		expect:
		AudioFiles.createDiscFile(file).discNum == discNum
		where:
		discNum << [1, 2, 11, 42]
	}
	
	def 'createDiscFile fails when called on a track'() {
		given:
		def file = new File('D01T02 TrackName.wav')
		when:
		AudioFiles.createDiscFile(file)
		then:
		thrown(IllegalArgumentException)
	}
	
	def 'createTrackFile works with valid file name'() {
		given:
		def file = new File("D${discNum}T${trackNum} TrackName.flac")
		expect:
		def track = AudioFiles.createTrackFile(file)
		track.discNum == discNum
		track.trackNum == trackNum
		where:
		discNum	| trackNum
		1		| 10
		3		| 3
		5		| 24
	}
	
	def 'createTrackFile fails when called on a disc'() {
		given:
		def file = new File('D01 DiscName.wav')
		when:
		AudioFiles.createTrackFile(file)
		then:
		thrown(IllegalArgumentException)
	}
}
