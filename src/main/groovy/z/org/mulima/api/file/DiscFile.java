package z.org.mulima.api.file;

import z.org.mulima.api.meta.Disc;

public interface DiscFile extends AudioFile {
	Disc getMeta();
	int getDiscNum();
}
