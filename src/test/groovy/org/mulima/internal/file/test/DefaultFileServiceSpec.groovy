package org.mulima.internal.file.test

import org.mulima.api.file.CachedFile
import org.mulima.api.file.FileService
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.file.audio.DiscFile
import org.mulima.api.file.audio.TrackFile
import org.mulima.internal.file.DefaultFileService
import org.mulima.util.FileUtil

import spock.lang.Specification

class DefaultFileServiceSpec extends Specification {
	FileService service = new DefaultFileService()
	
	def setup() {
		service.createCachedFile(_, _) >> Mock(CachedFile)
	}
	
	def 'createDiscFile works with valid file name'() {
		given:
		def file = new File("D${discNum} DiscName.flac")
		expect:
		service.createDiscFile(file).discNum == discNum
		where:
		discNum << [1, 2, 11, 42]
	}
	
	def 'createDiscFile works with valid file name 2'() {
		given:
		def file = new File("DiscName (${discNum}).flac")
		expect:
		service.createDiscFile(file).discNum == discNum
		where:
		discNum << [1, 2, 11, 42]
	}
	
	def 'createDiscFile works with valid file name 3'() {
		given:
		def file = new File("DiscName.flac")
		expect:
		service.createDiscFile(file).discNum == 1
	}
	
	def 'createDiscFile fails when called on a track'() {
		given:
		def file = new File('D01T02 TrackName.wav')
		when:
		service.createDiscFile(file)
		then:
		thrown(IllegalArgumentException)
	}
	
	def 'createDiscFile fails when called on non-audio file'() {
		given:
		def file = new File('D01 Test.txt')
		when:
		service.createDiscFile(file)
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
		def disc = service.createDiscFile(source, dir, format)
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
		def track = service.createTrackFile(file)
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
		def file = new File(fileName)
		when:
		service.createTrackFile(file)
		then:
		thrown(IllegalArgumentException)
		where:
		fileName << ['D01 DiscName.wav', 'DiscName.wav']
	}
	
	def 'createTrackFile fails when called on non-audio file'() {
		given:
		def file = new File('D01T01 Test.txt')
		when:
		service.createTrackFile(file)
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
		def track = service.createTrackFile(source, dir, format)
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
		def disc = service.createAudioFile(file)
		disc instanceof DiscFile
		disc.discNum == discNum
		where:
		discNum << [1, 2, 11, 42]
	}
	
	def 'createAudioFile works with track name'() {
		given:
		def file = new File("D${discNum}T${trackNum} TrackName.flac")
		expect:
		def track = service.createAudioFile(file)
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
