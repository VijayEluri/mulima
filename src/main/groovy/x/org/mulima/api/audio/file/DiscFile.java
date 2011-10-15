package x.org.mulima.api.audio.file;

import z.org.mulima.api.meta.Disc;

public interface DiscFile extends AudioFile {
	Disc getMeta();
	int getDiscNum();
}
