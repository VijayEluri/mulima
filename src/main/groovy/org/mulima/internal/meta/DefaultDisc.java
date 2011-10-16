package org.mulima.internal.meta;

import java.util.SortedSet;
import java.util.TreeSet;

import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Track;


public class DefaultDisc extends AbstractMetadata implements Disc {
	private final SortedSet<Track> tracks = new TreeSet<Track>();
	
	@Override
	public int getNum() {
		return Integer.valueOf(getFirst(GenericTag.DISC_NUMBER));
	}

	@Override
	public SortedSet<Track> getTracks() {
		return tracks;
	}

	@Override
	public Track getTrack(int num) {
		for (Track track : tracks) {
			if (track.getNum() == num) {
				return track;
			}
		}
		return null;
	}

	@Override
	public void tidy() {
		tidy(getTracks());
	}

	@Override
	public int compareTo(Disc o) {
		if (getNum() == o.getNum()) {
			return 0;
		} else {
			return getNum() < o.getNum() ? -1 : 1;
		}
	}
}
