package z.org.mulima.api.audio;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import z.org.mulima.api.audio.impl.DefaultDiscFile;
import z.org.mulima.api.audio.impl.DefaultTrackFile;

public class AudioFiles {
	private static final Pattern DISC_REGEX = Pattern.compile("^D(\\d+)[^T\\d]");
	private static final Pattern TRACK_REGEX = Pattern.compile("^D(\\d+)T(\\d+)");
	
	private AudioFiles() {
		throw new AssertionError("non-instantiable");
	}
	
	public static DiscFile createDiscFile(File file) {
		Matcher matcher = DISC_REGEX.matcher(file.getName());
		if (matcher.find()) {
			int discNum = Integer.valueOf(matcher.group(1));
			return new DefaultDiscFile(file, discNum);	
		} else {
			throw new IllegalArgumentException("File name must match pattern: " + DISC_REGEX.pattern());
		}
	}
	
	public static TrackFile createTrackFile(File file) {
		Matcher matcher = TRACK_REGEX.matcher(file.getName());
		if (matcher.find()) {
			int discNum = Integer.valueOf(matcher.group(1));
			int trackNum = Integer.valueOf(matcher.group(2));
			return new DefaultTrackFile(file, discNum, trackNum);	
		} else {
			throw new IllegalArgumentException("File name must match pattern: " + TRACK_REGEX.pattern());
		}
	}
}
