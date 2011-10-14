package z.org.mulima.api.audio.impl;

import java.io.File;

import z.org.mulima.api.audio.TrackFile;

public class DefaultTrackFile extends AbstractAudioFile implements TrackFile {
	private final int discNum;
	private final int trackNum;
	
	public DefaultTrackFile(File file, int discNum, int trackNum) {
		super(file);
		if (discNum < 0) {
			throw new IllegalArgumentException("Disc cannot be less than zero.");
		}
		this.discNum = discNum;
		if (trackNum < 0) {
			throw new IllegalArgumentException("Track cannot be less than zero.");
		}
		this.trackNum = trackNum;
	}
	
	@Override
	public int getDiscNum() {
		return discNum;
	}

	@Override
	public int getTrackNum() {
		return trackNum;
	}
}
