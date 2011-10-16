package org.mulima.internal.audio.file;

import java.io.File;

import org.mulima.api.audio.file.TrackFile;
import org.mulima.api.meta.Track;


public class DefaultTrackFile extends AbstractAudioFile implements TrackFile {
	private final Track track;
	private final int discNum;
	private final int trackNum;
	
	public DefaultTrackFile(File file, Track track) {
		super(file);
		if (track == null) {
			throw new NullPointerException("Track cannot be null.");
		}
		this.track = track;
		this.discNum = -1;
		this.trackNum = -1;
	}
	
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
		this.track = null;
	}
	
	@Override
	public int getDiscNum() {
		if (track == null) {
			return discNum;
		} else {
			return track.getDiscNum();
		}
	}

	@Override
	public int getTrackNum() {
		if (track == null) {
			return trackNum;
		} else {
			return track.getNum();
		}
	}

	@Override
	public Track getMeta() {
		return track;
	}
}
