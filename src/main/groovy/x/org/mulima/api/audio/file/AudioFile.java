package x.org.mulima.api.audio.file;

import java.io.File;

import x.org.mulima.api.audio.AudioFormat;
import z.org.mulima.api.meta.Metadata;

public interface AudioFile {
	File getFile();
	AudioFormat getFormat();
	Metadata getMeta();
}
