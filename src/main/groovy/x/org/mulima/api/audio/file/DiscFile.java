package x.org.mulima.api.audio.file;

import x.org.mulima.api.meta.Disc;

public interface DiscFile extends AudioFile {
	Disc getMeta();
	int getDiscNum();
}
