package org.mulima.internal.audio.file;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.audio.file.AudioFile;
import org.mulima.api.audio.file.AudioFileFactory;
import org.mulima.api.audio.file.DiscFile;
import org.mulima.api.audio.file.TrackFile;
import org.mulima.api.file.CachedFileFactory;
import org.mulima.api.meta.Album;
import org.mulima.util.FileUtil;


public class DefaultAudioFileFactory implements AudioFileFactory {
	private static final Pattern DISC_REGEX = Pattern.compile("^D(\\d+)[^T\\d]");
	private static final Pattern TRACK_REGEX = Pattern.compile("^D(\\d+)T(\\d+)");
	private final CachedFileFactory factory;
	
	public DefaultAudioFileFactory(CachedFileFactory factory) {
		this.factory = factory;
	}
	
	public DiscFile createDiscFile(File file) {
		Matcher matcher = DISC_REGEX.matcher(file.getName());
		if (matcher.find()) {
			int discNum = Integer.valueOf(matcher.group(1));
			Album album = factory.valueOf(new File(file.getParentFile(), Album.FILE_NAME), Album.class).getValue();
			if (album == null) {
				return new DefaultDiscFile(file, discNum);	
			} else {
				return new DefaultDiscFile(file, album.getDisc(discNum));
			}
		} else {
			throw new IllegalArgumentException("File name must match pattern: " + DISC_REGEX.pattern());
		}
	}
	
	public DiscFile createDiscFile(DiscFile source, File newDir, AudioFormat newFormat) {
		File newFile = createFile(source, newDir, newFormat);
		if (source.getMeta() == null) {
			return new DefaultDiscFile(newFile, source.getDiscNum());
		} else {
			return new DefaultDiscFile(newFile, source.getMeta());
		}
	}
	
	public TrackFile createTrackFile(File file) {
		Matcher matcher = TRACK_REGEX.matcher(file.getName());
		if (matcher.find()) {
			int discNum = Integer.valueOf(matcher.group(1));
			int trackNum = Integer.valueOf(matcher.group(2));
			Album album = factory.valueOf(new File(file.getParentFile(), Album.FILE_NAME), Album.class).getValue();
			if (album == null) {
				return new DefaultTrackFile(file, discNum, trackNum);
			} else {
				return new DefaultTrackFile(file, album.getDisc(discNum).getTrack(trackNum));
			}
		} else {
			throw new IllegalArgumentException("File name must match pattern: " + TRACK_REGEX.pattern());
		}
	}
	
	public TrackFile createTrackFile(TrackFile source, File newDir, AudioFormat newFormat) {
		File newFile = createFile(source, newDir, newFormat);
		if (source.getMeta() == null) {
			return new DefaultTrackFile(newFile, source.getDiscNum(), source.getTrackNum());	
		} else {
			return new DefaultTrackFile(newFile, source.getMeta());
		}
		
	}
	
	public AudioFile createAudioFile(File file) {
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
	
	public AudioFile createAudioFile(AudioFile source, File newDir, AudioFormat newFormat) {
		if (source instanceof DiscFile) {
			return createDiscFile((DiscFile) source, newDir, newFormat);
		} else if (source instanceof TrackFile) {
			return createTrackFile((TrackFile) source, newDir, newFormat);
		} else {
			throw new IllegalArgumentException("Unsupported audio file.");
		}
	}
	
	private File createFile(AudioFile source, File newDir, AudioFormat newFormat) {
		String baseName = FileUtil.getBaseName(source.getFile());
		return new File(newDir, baseName + "." + newFormat.getExtension());
	}

	@Override
	public AudioFile parse(File file) {
		return createAudioFile(file);
	}
}
