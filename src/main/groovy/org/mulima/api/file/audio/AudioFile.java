package org.mulima.api.file.audio;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.file.FileHolder;
import org.mulima.api.meta.Metadata;


public interface AudioFile extends FileHolder {
	AudioFormat getFormat();
	Metadata getMeta();
}
