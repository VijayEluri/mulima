package org.mulima.internal.meta.test

import org.mulima.internal.meta.DefaultCuePoint

import spock.lang.Specification

class DefaultCuePointSpec extends Specification {
	def 'constructor succeeds with valid parameters'() {
		when:
		new DefaultCuePoint(1, 1, '23:32:23')
		then:
		notThrown(IllegalArgumentException)
	}
	
	def 'constructor fails when track is negative'() {
		when:
		new DefaultCuePoint(-1, 1, '23:32:23')
		then:
		thrown(IllegalArgumentException)
	}
	
	def 'constructor fails when index is negative'() {
		when:
		new DefaultCuePoint(1, -1, '23:32:23')
		then:
		thrown(IllegalArgumentException)
	}
	
	def 'constructor fails when minutes are negative'() {
		when:
		new DefaultCuePoint(1, 1, '-23:32:23')
		then:
		thrown(IllegalArgumentException)
	}
	
	def 'constructor fails when seconds are negative'() {
		when:
		new DefaultCuePoint(1, 1, '23:-32:23')
		then:
		thrown(IllegalArgumentException)
	}
	
	def 'constructor fails when seconds are greater than 59'() {
		when:
		new DefaultCuePoint(1, 1, '23:60:23')
		then:
		thrown(IllegalArgumentException)
	}
	
	def 'constructor fails when frames are negative'() {
		when:
		new DefaultCuePoint(1, 1, '23:32:-23')
		then:
		thrown(IllegalArgumentException)
	}
	
	def 'constructor fails when frames are greates than 74'() {
		when:
		new DefaultCuePoint(1, 1, '23:32:75')
		then:
		thrown(IllegalArgumentException)
	}
}
