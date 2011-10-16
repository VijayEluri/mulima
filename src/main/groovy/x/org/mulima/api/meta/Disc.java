package x.org.mulima.api.meta;

import java.util.SortedSet;

public interface Disc extends Metadata, Comparable<Disc> {
	int getNum();
	SortedSet<Track> getTracks();
	Track getTrack(int num);
	void tidy();
}
