package z.org.mulima.api.audio.impl;

import java.io.File;

import z.org.mulima.api.audio.AudioFile;
import z.org.mulima.api.audio.AudioFormat;

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
