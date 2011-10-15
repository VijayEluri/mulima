package z.org.mulima.api.file.test

import org.mulima.util.FileUtil

import spock.lang.Specification
import z.org.mulima.api.file.AudioFiles
import z.org.mulima.api.file.AudioFormat
import z.org.mulima.api.file.DiscFile
import z.org.mulima.api.file.TrackFile

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
	
	def 'createDiscFile works with valid dir, format, and disc file'() {
		given:
		def source = Mock(DiscFile)
		source.discNum >> discNum
		source.file >> new File(fileName)
		def dir = new File('test')
		def format = AudioFormat.WAVE
		expect:
		def disc = AudioFiles.createDiscFile(source, dir, format)
		FileUtil.getBaseName(source.file) == FileUtil.getBaseName(disc.file)
		disc.format == format
		disc.file.parentFile == dir
		disc.discNum == discNum
		where:
		discNum	| fileName
		1		| 'test.mp3'
		5		| 'alwkefj.awlekfjlawif.blah'
		100		| 'aasdf.m4a'
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
	
	def 'createTrackFile works with valid dir, format, and track file'() {
		given:
		def source = Mock(TrackFile)
		source.discNum >> discNum
		source.trackNum >> trackNum
		source.file >> new File(fileName)
		def dir = new File('test')
		def format = AudioFormat.WAVE
		expect:
		def track = AudioFiles.createTrackFile(source, dir, format)
		FileUtil.getBaseName(source.file) == FileUtil.getBaseName(track.file)
		track.format == format
		track.file.parentFile == dir
		track.discNum == discNum
		track.trackNum == trackNum
		where:
		discNum	| trackNum	| fileName
		1		| 40		| 'test.mp3'
		4		| 50		| 'testing.ogg'
	}
	
	def 'createAudioFile works with disc name'() {
		given:
		def file = new File("D${discNum} DiscName.flac")
		expect:
		def disc = AudioFiles.createAudioFile(file)
		disc instanceof DiscFile
		disc.discNum == discNum
		where:
		discNum << [1, 2, 11, 42]
	}
	
	def 'createAudioFile works with track name'() {
		given:
		def file = new File("D${discNum}T${trackNum} TrackName.flac")
		expect:
		def track = AudioFiles.createAudioFile(file)
		track instanceof TrackFile
		track.discNum == discNum
		track.trackNum == trackNum
		where:
		discNum	| trackNum
		1		| 10
		3		| 3
		5		| 24
	}
}
