package org.mulima.internal.audio.file.impl.test

import org.mulima.internal.audio.file.DefaultDiscFile

import spock.lang.Specification

class DefaultDiscFileSpec extends Specification {
	def 'constructor works with positive disc number'() {
		when:
		new DefaultDiscFile(new File('dummy.wav'), discNum)
		then:
		notThrown(IllegalArgumentException)
		where:
		discNum << [0, 1, 2, 3, 1000]
	}
	
	def 'constructor fails with null file'() {
		when: 
		new DefaultDiscFile(null, 1)
		then:
		thrown(NullPointerException)
	}
	
	def 'constructor fails with invalid extension on file'() {
		when:
		new DefaultDiscFile(new File('blah.zzz'), 1)
		then:
		thrown(IllegalArgumentException)
	}
	
	def 'constructor fails with negative discNum'() {
		when:
		new DefaultDiscFile(new File('dummy.wav'), discNum)
		then:
		thrown(IllegalArgumentException)
		where:
		discNum << [-1, -5, -100]
	}
}
