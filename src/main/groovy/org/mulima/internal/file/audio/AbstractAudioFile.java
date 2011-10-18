package org.mulima.internal.file.audio;

import java.io.File;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.file.audio.AudioFile;


public abstract class AbstractAudioFile implements AudioFile {
	private final File file;
	private final AudioFormat format;
	
	public AbstractAudioFile(File file) {
		this.file = file;
		this.format = AudioFormat.valueOf(file);
	}
	
	@Override
	public File getFile() {
		return file;
	}

	@Override
	public AudioFormat getFormat() {
		return format;
	}
}
