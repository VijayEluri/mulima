package x.org.mulima.internal.audio.file;

import java.io.File;

import x.org.mulima.api.audio.AudioFormat;
import x.org.mulima.api.audio.file.AudioFile;

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
