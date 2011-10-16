package org.mulima.api.audio.file;

import org.mulima.api.meta.Disc;

public interface DiscFile extends AudioFile {
	Disc getMeta();
	int getDiscNum();
}
