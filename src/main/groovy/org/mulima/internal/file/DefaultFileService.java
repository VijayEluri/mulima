package org.mulima.internal.file;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.file.CachedDir;
import org.mulima.api.file.CachedFile;
import org.mulima.api.file.Digest;
import org.mulima.api.file.FileComposer;
import org.mulima.api.file.FileParser;
import org.mulima.api.file.FileService;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.DiscFile;
import org.mulima.api.file.audio.TrackFile;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;
import org.mulima.internal.file.audio.DefaultDiscFile;
import org.mulima.internal.file.audio.DefaultTrackFile;
import org.mulima.internal.meta.AlbumXmlDao;
import org.mulima.internal.meta.CueSheetParser;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DefaultFileService implements FileService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileService.class);
	private static final Pattern[] DISC_REGEX = { Pattern.compile("^D(\\d++)(?!T\\d+)"), Pattern.compile("\\((\\d+)\\)\\..*?$"), Pattern.compile("^(?!D\\d{1," + (Integer.MAX_VALUE - 4) + "}T\\d)") };
	private static final Pattern[] TRACK_REGEX = { Pattern.compile("^D(\\d+)T(\\d+)") };
	private final Map<Class<?>, FileParser<?>> parsers = new HashMap<Class<?>, FileParser<?>>();
	private final Map<Class<?>, FileComposer<?>> composers = new HashMap<Class<?>, FileComposer<?>>();
	private final Map<Class<?>, Map<File, CachedFile<?>>> filesCache = new HashMap<Class<?>, Map<File, CachedFile<?>>>();
	
	public DefaultFileService() {
		registerParser(AudioFile.class, new AudioFileParser());
		registerParser(CueSheet.class, new CueSheetParser());
		registerParser(Digest.class, new DigestDao());
		registerParser(Album.class, new AlbumXmlDao());
		registerComposer(Album.class, new AlbumXmlDao());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> FileParser<T> getParser(Class<T> type) {
		return (FileParser<T>) parsers.get(type);
	}
	
	public <T> void registerParser(Class<T> type, FileParser<T> parser) {
		parsers.put(type, parser);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> FileComposer<T> getComposer(Class<T> type) {
		return (FileComposer<T>) composers.get(type);
	}
	
	public <T> void registerComposer(Class<T> type, FileComposer<T> composer) {
		composers.put(type, composer);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> CachedFile<T> createCachedFile(Class<T> type, File file) {
		Map<File, CachedFile<?>> tempCache;
		if (filesCache.containsKey(type)) {
			tempCache = filesCache.get(type);
		} else {
			tempCache = new HashMap<File, CachedFile<?>>();
			filesCache.put(type, tempCache);
		}
		
		CachedFile<T> cachedFile;
		if (tempCache.containsKey(file)) {
			cachedFile = (CachedFile<T>) tempCache.get(file);
		} else {
			cachedFile = new DefaultCachedFile<T>(getParser(type), file);
			tempCache.put(file, cachedFile);
		}
		return cachedFile;
	}

	@Override
	public <T> CachedDir<T> createCachedDir(Class<T> type, File dir) {
		return new DefaultCachedDir<T>(this, type, dir);
	}

	@Override
	public <T> CachedDir<T> createCachedDir(Class<T> type, File dir, FileFilter filter) {
		return new DefaultCachedDir<T>(this, type, dir, filter);
	}
	
	@Override
	public DiscFile createDiscFile(File file) {
		if (!isAudioFile(file)) {
			throw new IllegalArgumentException("File (" + file.getName() + ") is not a supported audio file.");
		}
		for (Pattern pattern : DISC_REGEX) {
			Matcher matcher = pattern.matcher(file.getName());
			if (matcher.find()) {
				int discNum = matcher.groupCount() == 0 ? 1 : Integer.valueOf(matcher.group(1));
				Album album = createCachedFile(Album.class, new File(file.getParentFile(), Album.FILE_NAME)).getValue();
				if (album == null) {
					return new DefaultDiscFile(file, discNum);	
				} else {
					return new DefaultDiscFile(file, album.getDisc(discNum));
				}
			}
		}
		throw new IllegalArgumentException(messageForNoMatch(file.getName(), DISC_REGEX));
	}
	
	@Override
	public DiscFile createDiscFile(DiscFile source, File newDir, AudioFormat newFormat) {
		File newFile = createFile(source, newDir, newFormat);
		if (source.getMeta() == null) {
			return new DefaultDiscFile(newFile, source.getDiscNum());
		} else {
			return new DefaultDiscFile(newFile, source.getMeta());
		}
	}
	
	@Override
	public TrackFile createTrackFile(File file) {
		if (!isAudioFile(file)) {
			throw new IllegalArgumentException("File (" + file.getName() + ") is not a supported audio file.");
		}
		for (Pattern pattern : TRACK_REGEX) {
			Matcher matcher = pattern.matcher(file.getName());
			if (matcher.find()) {
				int discNum = Integer.valueOf(matcher.group(1));
				int trackNum = Integer.valueOf(matcher.group(2));
				Album album = createCachedFile(Album.class, new File(file.getParentFile(), Album.FILE_NAME)).getValue();
				if (album == null) {
					return new DefaultTrackFile(file, discNum, trackNum);
				} else {
					return new DefaultTrackFile(file, album.getDisc(discNum).getTrack(trackNum));
				}
			}
		}
		throw new IllegalArgumentException(messageForNoMatch(file.getName(), TRACK_REGEX));
	}
	
	@Override
	public TrackFile createTrackFile(TrackFile source, File newDir, AudioFormat newFormat) {
		File newFile = createFile(source, newDir, newFormat);
		if (source.getMeta() == null) {
			return new DefaultTrackFile(newFile, source.getDiscNum(), source.getTrackNum());	
		} else {
			return new DefaultTrackFile(newFile, source.getMeta());
		}
		
	}
	
	@Override
	public AudioFile createAudioFile(File file) {
		try {
			return createDiscFile(file);
		} catch (IllegalArgumentException e) {
			try {
				return createTrackFile(file);
			} catch (IllegalArgumentException e2) {
				throw new IllegalArgumentException(messageForNoMatch(file.getName(), DISC_REGEX, TRACK_REGEX));
			}
		}
	}
	
	@Override
	public AudioFile createAudioFile(AudioFile source, File newDir, AudioFormat newFormat) {
		if (source == null) {
			throw new IllegalArgumentException("Source file cannot be null.");
		} else if (source instanceof DiscFile) {
			return createDiscFile((DiscFile) source, newDir, newFormat);
		} else if (source instanceof TrackFile) {
			return createTrackFile((TrackFile) source, newDir, newFormat);
		} else {
			throw new IllegalArgumentException("Unsupported audio file: " + source);
		}
	}
	
	private File createFile(AudioFile source, File newDir, AudioFormat newFormat) {
		String baseName = FileUtil.getBaseName(source.getFile());
		return new File(newDir, baseName + "." + newFormat.getExtension());
	}
	
	private boolean isAudioFile(File file) {
		for (AudioFormat format : AudioFormat.values()) {
			if (file.getName().endsWith(format.getExtension())) {
				return true;
			}
		}
		return false;
	}
	
	private String messageForNoMatch(String fileName, Pattern[]... patternArrays) {
		StringBuilder builder = new StringBuilder();
		builder.append("File name (");
		builder.append(fileName);
		builder.append(") must match pattern: ");
		boolean first = true;
		for (Pattern[] patterns : patternArrays) {
			for (Pattern pattern : patterns) {
				if (!first) {
					builder.append(" or ");
				}
				builder.append(pattern.pattern());
				first = false;
			}
		}
		return builder.toString();
	}
	
	private class AudioFileParser implements FileParser<AudioFile> {
		@Override
		public AudioFile parse(File file) {
			try {
				return createAudioFile(file);
			} catch (IllegalArgumentException e) {
				LOGGER.debug("Invalid file: {}", e.getMessage());
				return null;
			}
		}
	}
}
