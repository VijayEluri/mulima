package org.mulima.internal.meta.test

import org.mulima.api.meta.Disc
import org.mulima.api.meta.Metadata
import org.mulima.api.meta.Track
import org.mulima.api.meta.test.DiscSpec
import org.mulima.internal.meta.DefaultDisc
import org.mulima.internal.meta.DefaultTrack

class DefaultDiscSpec extends DiscSpec {
	def setupSpec() {
		factory.registerImplementation(Metadata, DefaultDisc)
		factory.registerImplementation(Disc, DefaultDisc)
		factory.registerImplementation(Track, DefaultTrack)
	}
	
	void addTrack(Disc disc, Track track) {
		disc.tracks.add(track)
	}
}
