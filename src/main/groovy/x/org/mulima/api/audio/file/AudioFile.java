package x.org.mulima.api.audio.file;

import x.org.mulima.api.audio.AudioFormat;
import x.org.mulima.api.file.FileHolder;
import x.org.mulima.api.meta.Metadata;

public interface AudioFile extends FileHolder {
	AudioFormat getFormat();
	Metadata getMeta();
}
