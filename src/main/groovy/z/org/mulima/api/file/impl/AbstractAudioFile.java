package z.org.mulima.api.file.impl;

import java.io.File;

import z.org.mulima.api.file.AudioFile;
import z.org.mulima.api.file.AudioFormat;

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
