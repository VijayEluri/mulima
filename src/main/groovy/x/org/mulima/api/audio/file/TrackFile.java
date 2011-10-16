package x.org.mulima.api.audio.file;

import x.org.mulima.api.meta.Track;

public interface TrackFile extends AudioFile {
	Track getMeta();
	int getDiscNum();
	int getTrackNum();
}
