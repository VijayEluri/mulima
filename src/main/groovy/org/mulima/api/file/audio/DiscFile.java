package org.mulima.api.file.audio;

import org.mulima.api.meta.Disc;

public interface DiscFile extends AudioFile {
	Disc getMeta();
	int getDiscNum();
}
