package z.org.mulima.api.meta.test

import spock.lang.Specification
import z.org.mulima.api.meta.GenericTag
import z.org.mulima.api.meta.Track

class TrackSpec extends Specification {
	def 'getNum gets value from tag value'() {
		given:
		Track track = new Track()
		track.add(GenericTag.TRACK_NUMBER, Integer.toString(num))
		expect:
		track.getNum() == num
		where:
		num << [1, 2, 3]
	}
	
	def 'tracks sort by disc num first'() {
		given:
		Track track1 = new Track()
		track1.add(GenericTag.TRACK_NUMBER, Integer.toString(discNum1))
		track1.add(GenericTag.TRACK_NUMBER, Integer.toString(trackNum1))
		and:
		Track track2 = new Track()
		track2.add(GenericTag.TRACK_NUMBER, Integer.toString(discNum2))
		track2.add(GenericTag.TRACK_NUMBER, Integer.toString(trackNum2))
		expect:
		track1 > track2
		where:
		discNum1 | trackNum1 | discNum2 | trackNum2
		2 | 1 | 1 | 2
	}
}
