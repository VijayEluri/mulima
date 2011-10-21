package org.mulima.api.meta.test

import org.mulima.api.meta.CueSheet
import org.mulima.api.meta.CuePoint

abstract class CueSheetSpec extends MetadataSpec<CueSheet> {
	
	def 'getCuePoints returns an empty set if none are added yet'() {
		expect:
		meta.cuePoints.isEmpty()
	}
	
	def 'getCuePoints returns only the cue points with an index of 1'() {
		given:
		meta.allCuePoints.add(mockCuePoint(1, 0, '00:00:54'))
		meta.allCuePoints.add(mockCuePoint(1, 1, '00:05:21'))
		meta.allCuePoints.add(mockCuePoint(1, 2, '05:10:31'))
		meta.allCuePoints.add(mockCuePoint(2, 0, '07:10:24'))
		meta.allCuePoints.add(mockCuePoint(2, 1, '10:30:04'))
		expect:
		meta.cuePoints.size() == 2
		meta.cuePoints.each {
			it.index == 1
		}
	}
	
	def 'getAllCuePoints returns an empty set if none are added yet'() {
		expect:
		meta.allCuePoints.isEmpty()
	}
	
	def 'getAllCuePoints returns all cue points'() {
		given:
		meta.allCuePoints.add(mockCuePoint(1, 0, '00:00:54'))
		meta.allCuePoints.add(mockCuePoint(1, 1, '00:05:21'))
		meta.allCuePoints.add(mockCuePoint(1, 2, '05:10:31'))
		meta.allCuePoints.add(mockCuePoint(2, 0, '07:10:24'))
		meta.allCuePoints.add(mockCuePoint(2, 1, '10:30:04'))
		expect:
		meta.allCuePoints.size() == 5
	}
	
	CuePoint mockCuePoint(int track, int index, String time) {
		CuePoint point = Mock(CuePoint)
		point.track >> track
		point.index >> index
		point.time >> time
		point.compareTo(_) >> 1
		return point
	}
}
