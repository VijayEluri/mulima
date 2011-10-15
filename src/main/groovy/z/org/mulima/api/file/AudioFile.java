package z.org.mulima.api.file;

import java.io.File;

import z.org.mulima.api.meta.Metadata;

public interface AudioFile {
	File getFile();
	AudioFormat getFormat();
	Metadata getMeta();
}
