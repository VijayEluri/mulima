package org.mulima.internal.meta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.api.meta.CuePoint;


public class DefaultCuePoint implements CuePoint {
	private static final Pattern TIME_REGEX = Pattern.compile("^(\\d{2}):(\\d{2}):(\\d{2})$");
	
	private final int track;
	private final int index;
	private final String time;
	
	public DefaultCuePoint(int track, int index, String time) {
		if (track > 0) {
			this.track = track;
		} else {
			throw new IllegalArgumentException("Track number must be greater than 0.");
		}
		
		if (index >= 0) {
			this.index = index;
		} else {
			throw new IllegalArgumentException("Index number must be 0 or greater.");
		}
		
		if (verifyTimeFormat(time)) {
			this.time = time;	
		} else {
			throw new IllegalArgumentException("Time must match the following format: " + TIME_REGEX.pattern());
		}
	}
	
	private boolean verifyTimeFormat(String time) {
		Matcher matcher = TIME_REGEX.matcher(time);
		if (matcher.find()) {
			int minutes = Integer.valueOf(matcher.group(1));
			if (minutes < 0) {
				return false;
			}
			int seconds = Integer.valueOf(matcher.group(2));
			if (seconds < 0 || seconds >= 60) {
				return false;
			}
			int frames = Integer.valueOf(matcher.group(3));
			if (frames < 0 || frames >= 75) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int getTrack() {
		return track;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public String getTime() {
		return time;
	}

	@Override
	public int compareTo(CuePoint other) {
		if (track == other.getTrack()) {
			if (index == other.getIndex()) {
				if (time == other.getTime()) {
					return 0;
				} else {
					return time.compareTo(other.getTime());
				}
			} else {
				return index < other.getIndex() ? -1 : 1;
			}
		} else {
			return track < other.getTrack() ? -1 : 1;
		}
	}
}
