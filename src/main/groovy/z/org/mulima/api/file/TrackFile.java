package z.org.mulima.api.file;

import z.org.mulima.api.meta.Track;

public interface TrackFile extends AudioFile {
	Track getMeta();
	int getDiscNum();
	int getTrackNum();
}