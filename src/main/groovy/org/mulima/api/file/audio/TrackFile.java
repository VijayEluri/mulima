package org.mulima.api.file.audio;

import org.mulima.api.meta.Track;

public interface TrackFile extends AudioFile {
	Track getMeta();
	int getDiscNum();
	int getTrackNum();
}
