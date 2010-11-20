package com.andrewoberstar.library.meta.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.andrewoberstar.library.meta.GenericTag;
import com.andrewoberstar.library.meta.Track;

public class TrackTest {
	@Test
	public void getNum() {
		Track track = new Track();
		track.getTags().add(GenericTag.TRACK_NUMBER, Integer.toString(1));
		assertEquals(1, track.getNum());
	}
}
