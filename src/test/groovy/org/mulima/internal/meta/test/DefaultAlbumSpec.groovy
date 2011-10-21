package org.mulima.internal.meta.test

import org.mulima.api.meta.Album
import org.mulima.api.meta.Disc
import org.mulima.api.meta.Metadata
import org.mulima.api.meta.Track
import org.mulima.api.meta.test.AlbumSpec
import org.mulima.internal.meta.DefaultAlbum
import org.mulima.internal.meta.DefaultDisc
import org.mulima.internal.meta.DefaultTrack

class DefaultAlbumSpec extends AlbumSpec {
	def setupSpec() {
		factory.with {
			registerImplementation Metadata, DefaultAlbum
			registerImplementation Album, DefaultAlbum
			registerImplementation Disc, DefaultDisc
			registerImplementation Track, DefaultTrack
		}
	}
	
	void addDisc(Album album, Disc disc) {
		album.discs.add(disc)
	}
	
	void addTrack(Disc disc, Track track) {
		disc.tracks.add(track)
	}
}
