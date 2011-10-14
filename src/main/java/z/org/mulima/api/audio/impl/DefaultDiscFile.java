package z.org.mulima.api.audio.impl;

import java.io.File;

import z.org.mulima.api.audio.DiscFile;

public class DefaultDiscFile extends AbstractAudioFile implements DiscFile {
	private final int discNum;
	
	public DefaultDiscFile(File file, int discNum) {
		super(file);
		if (discNum < 0) {
			throw new IllegalArgumentException("Disc cannot be less than zero.");
		}
		this.discNum = discNum;
	}
	
	@Override
	public int getDiscNum() {
		return discNum;
	}
}
