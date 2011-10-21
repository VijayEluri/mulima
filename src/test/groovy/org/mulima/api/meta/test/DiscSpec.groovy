package org.mulima.api.meta.test

import org.mulima.api.meta.Disc
import org.mulima.api.meta.GenericTag
import org.mulima.api.meta.Track

abstract class DiscSpec extends MetadataSpec<Disc> {
	abstract void addTrack(Disc disc, Track track)
	
	def 'getNum gets value from tag value'() {
		given:
		meta.add(GenericTag.DISC_NUMBER, Integer.toString(num))
		expect:
		meta.getNum() == num
		where:
		num << [1, 2, 3]
	}
	
	def 'getTrack picks the track by number'() {
		given:
		addTrack(meta, createTrack(1))
		addTrack(meta, createTrack(2))
		addTrack(meta, createTrack(3))
		expect:
		meta.getTrack(2).num == 2
		meta.getTrack(3).num == 3
	}
	
	def 'discs sort by disc num'() {
		given:
		Disc disc1 = factory.newInstance(Disc)
		disc1.add(GenericTag.DISC_NUMBER, Integer.toString(discNum1))
		disc1.add(GenericTag.TRACK_NUMBER, Integer.toString(trackNum1))
		and:
		Disc disc2 = factory.newInstance(Disc)
		disc2.add(GenericTag.DISC_NUMBER, Integer.toString(discNum2))
		disc2.add(GenericTag.TRACK_NUMBER, Integer.toString(trackNum2))
		expect:
		disc1 > disc2
		where:
		discNum1 | trackNum1 | discNum2 | trackNum2
		2 | 1 | 1 | 2
	}
	
	Track createTrack(int num) {
		return factory.fromStringString([DISC_NUMBER:'1', TRACK_NUMBER:Integer.toString(num)], Track.class)
	}
}
