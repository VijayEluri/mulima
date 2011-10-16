package x.org.mulima.api.meta;

import java.util.SortedSet;

import x.org.mulima.api.meta.Disc;
import x.org.mulima.api.meta.Track;

public interface Album extends Metadata {
	SortedSet<Disc> getDiscs();
	Disc getDisc(int num);
	SortedSet<Track> flatten();
	void tidy();
}
