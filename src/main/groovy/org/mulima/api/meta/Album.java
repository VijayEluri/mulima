package org.mulima.api.meta;

import java.util.SortedSet;

import org.mulima.api.meta.Disc;
import org.mulima.api.meta.Track;


public interface Album extends Metadata {
	static final String FILE_NAME = "album.xml";
	SortedSet<Disc> getDiscs();
	Disc getDisc(int num);
	SortedSet<Track> flatten();
	void tidy();
}
