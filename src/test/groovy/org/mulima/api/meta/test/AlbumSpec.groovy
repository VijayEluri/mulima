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
	
	def 'flatten returns tracks with all of the data from their parent disc and album'() {
		given:
		Album album = factory.fromStringString([ARTIST:'Miles Davis', ALBUM:'Bitches Brew'], Album)
		Disc disc1 = factory.fromStringString([DISC_NUMBER:'1'], Disc)
		addTrack(disc1, factory.fromStringString([TRACK_NUMBER:'1', TITLE:'Pharaoh\'s Dance'], Track))
		addTrack(disc1, factory.fromStringString([TRACK_NUMBER:'2', TITLE:'Bitches Brew'], Track))
		addDisc(album, disc1)
		Disc disc2 = factory.fromStringString([DISC_NUMBER:'2'], Disc)
		addTrack(disc1, factory.fromStringString([TRACK_NUMBER:'1', TITLE:'Spanish Key'], Track))
		addTrack(disc1, factory.fromStringString([TRACK_NUMBER:'2', TITLE:'John McLaughlin'], Track))
		addTrack(disc1, factory.fromStringString([TRACK_NUMBER:'3', TITLE:'Miles Runs The Voodoo Down'], Track))
		addTrack(disc1, factory.fromStringString([TRACK_NUMBER:'4', TITLE:'Sanctuary'], Track))
		addDisc(album, disc2)
		expect:
		album.flatten().every { Track it ->
			it.isSet(GenericTag.ARTIST)
			it.isSet(GenericTag.ALBUM)
			it.isSet(GenericTag.DISC_NUMBER)
			it.isSet(GenericTag.TRACK_NUMBER)
			it.isSet(GenericTag.TITLE)
		} 
		
	}
	
	Disc createDisc(int num) {
		return factory.fromStringString([DISC_NUMBER:Integer.toString(num)], Disc.class)
	}
}
