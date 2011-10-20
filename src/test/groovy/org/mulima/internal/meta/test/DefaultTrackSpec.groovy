package org.mulima.internal.meta.test

import org.mulima.api.meta.GenericTag
import org.mulima.api.meta.Track
import org.mulima.internal.meta.DefaultTrack

import spock.lang.Specification

class DefaultTrackSpec extends Specification {
	def 'getNum gets value from tag value'() {
		given:
		Track track = new DefaultTrack()
		track.addAll(GenericTag.TRACK_NUMBER, Integer.toString(num))
		expect:
		track.getNum() == num
		where:
		num << [1, 2, 3]
	}
	
	def 'tracks sort by disc num first'() {
		given:
		Track track1 = new DefaultTrack()
		track1.addAll(GenericTag.TRACK_NUMBER, Integer.toString(discNum1))
		track1.addAll(GenericTag.TRACK_NUMBER, Integer.toString(trackNum1))
		and:
		Track track2 = new DefaultTrack()
		track2.addAll(GenericTag.TRACK_NUMBER, Integer.toString(discNum2))
		track2.addAll(GenericTag.TRACK_NUMBER, Integer.toString(trackNum2))
		expect:
		track1 > track2
		where:
		discNum1 | trackNum1 | discNum2 | trackNum2
		2 | 1 | 1 | 2
	}
}
