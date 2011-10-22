package org.mulima.internal.meta.test

import org.mulima.api.meta.Metadata
import org.mulima.api.meta.Track
import org.mulima.api.meta.test.TrackSpec
import org.mulima.internal.meta.DefaultTrack

class DefaultTrackSpec extends TrackSpec {
	def setupSpec() {
		factory.registerImplementation(Metadata, DefaultTrack)
		factory.registerImplementation(Track, DefaultTrack)
	}
}
