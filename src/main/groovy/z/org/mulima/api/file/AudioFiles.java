package z.org.mulima.api.file;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.util.FileUtil;

import z.org.mulima.api.file.impl.DefaultDiscFile;
import z.org.mulima.api.file.impl.DefaultTrackFile;

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
	
	public static DiscFile createDiscFile(DiscFile source, File newDir, AudioFormat newFormat) {
		File newFile = createFile(source, newDir, newFormat);
		return new DefaultDiscFile(newFile, source.getDiscNum());
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
	
	public static TrackFile createTrackFile(TrackFile source, File newDir, AudioFormat newFormat) {
		File newFile = createFile(source, newDir, newFormat);
		return new DefaultTrackFile(newFile, source.getDiscNum(), source.getTrackNum());
	}
	
	public static AudioFile createAudioFile(File file) {
		try {
			return createDiscFile(file);
		} catch (IllegalArgumentException e) {
			try {
				return createTrackFile(file);
			} catch (IllegalArgumentException e2) {
				throw new IllegalArgumentException("File name must match pattern: " + DISC_REGEX.pattern() + " or " + TRACK_REGEX.pattern());
			}
		}
	}
	
	public static AudioFile createAudioFile(AudioFile source, File newDir, AudioFormat newFormat) {
		if (source instanceof DiscFile) {
			return createDiscFile((DiscFile) source, newDir, newFormat);
		} else if (source instanceof TrackFile) {
			return createTrackFile((TrackFile) source, newDir, newFormat);
		} else {
			throw new IllegalArgumentException("Unsupported audio file.");
		}
	}
	
	private static File createFile(AudioFile source, File newDir, AudioFormat newFormat) {
		String baseName = FileUtil.getBaseName(source.getFile());
		return new File(newDir, baseName + "." + newFormat.getExtension());
	}
}
