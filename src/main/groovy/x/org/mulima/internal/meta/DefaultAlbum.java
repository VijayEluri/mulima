package x.org.mulima.internal.meta;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import x.org.mulima.api.meta.Album;
import x.org.mulima.api.meta.Disc;
import x.org.mulima.api.meta.GenericTag;
import x.org.mulima.api.meta.Metadata;
import x.org.mulima.api.meta.Tag;
import x.org.mulima.api.meta.Track;

public class DefaultAlbum extends AbstractMetadata implements Album {
	private final SortedSet<Disc> discs = new TreeSet<Disc>();

	@Override
	public SortedSet<Disc> getDiscs() {
		return discs;
	}

	@Override
	public Disc getDisc(int num) {
		for (Disc disc : discs) {
			if (disc.getNum() == num) {
				return disc;
			}
		}
		return null;
	}

	@Override
	public SortedSet<Track> flatten() {
		SortedSet<Track> tracks = new TreeSet<Track>();
		for (Disc disc : discs) {
			for (Track track : disc.getTracks()) {
				Track temp = new DefaultTrack();
				for (Tag tag : GenericTag.values()) {
					if (track.isSet(tag)) {
						temp.add(tag, track.getAll(tag));
					} else if (disc.isSet(tag)) {
						temp.add(tag, disc.getAll(tag));
					} else if (this.isSet(tag)) {
						temp.add(tag, this.getAll(tag));
					}
				}
				tracks.add(temp);
			}
		}
		return tracks;
	}

	@Override
	public void tidy() {
		tidy(getDiscs());
	}
}
