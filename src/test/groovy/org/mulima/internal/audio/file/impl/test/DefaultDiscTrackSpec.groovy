package org.mulima.internal.audio.file.impl.test

import org.mulima.internal.audio.file.DefaultTrackFile

import spock.lang.Specification

class DefaultDiscTrackSpec extends Specification {
	def 'constructor works with positive disc number and positive track number'() {
		when:
		new DefaultTrackFile(new File('dummy.wav'), discNum, trackNum)
		then:
		notThrown(IllegalArgumentException)
		where:
		discNum	| trackNum
		0		| 0
		1		| 5
		5		| 1
		100		| 1000
	}
	
	def 'constructor fails with null file'() {
		when: 
		new DefaultTrackFile(null, 1, 1)
		then:
		thrown(NullPointerException)
	}
	
	def 'constructor fails with invalid extension on file'() {
		when:
		new DefaultTrackFile(new File('blah.zzz'), 1, 1)
		then:
		thrown(IllegalArgumentException)
	}
	
	def 'constructor fails with negative discNum'() {
		when:
		new DefaultTrackFile(new File('dummy.wav'), discNum, 1)
		then:
		thrown(IllegalArgumentException)
		where:
		discNum << [-1, -5, -100]
	}
	
	def 'constructor fails with negative trackNum'() {
		when:
		new DefaultTrackFile(new File('dummy.wav'), 1, trackNum)
		then:
		thrown(IllegalArgumentException)
		where:
		trackNum << [-1, -5, -100]
	}
}
