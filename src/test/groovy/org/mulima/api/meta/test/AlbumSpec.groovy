package org.mulima.api.meta.test

import org.mulima.api.meta.Album
import org.mulima.api.meta.Disc
import org.mulima.api.meta.GenericTag
import org.mulima.api.meta.Track

abstract class AlbumSpec extends MetadataSpec<Album> {	
	abstract void addDisc(Album album, Disc disc)
	abstract void addTrack(Disc disc, Track track)
	
	def 'getDisc picks the disc by number'() {
		given:
		addDisc(meta, createDisc(1))
		addDisc(meta, createDisc(2))
		addDisc(meta, createDisc(3))
		expect:
		meta.getDisc(2).num == 2
		meta.getDisc(3).num == 3
	}
	
	Disc createDisc(int num) {
		return factory.fromStringString([DISC_NUMBER:Integer.toString(num)], Disc.class)
	}
}
