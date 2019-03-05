package org.mulima.file;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.file.audio.ArtworkFile;
import org.mulima.file.audio.ArtworkFormat;
import org.mulima.file.audio.AudioFile;
import org.mulima.file.audio.AudioFormat;
import org.mulima.file.audio.DiscFile;
import org.mulima.file.audio.TrackFile;
import org.mulima.meta.Album;
import org.mulima.meta.AlbumXmlDao;
import org.mulima.meta.CueSheet;
import org.mulima.meta.CueSheetParser;
import org.mulima.util.FileUtil;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a file service.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class FileService {
  private static final Logger logger = LogManager.getLogger(FileService.class);
  private static final Pattern[] DISC_REGEX = {
      Pattern.compile("^D(\\d++)(?!T\\d+)"),
      Pattern.compile("\\((\\d+)\\)\\..*?$"),
      Pattern.compile("^(?!D\\d{1," + (Integer.MAX_VALUE - 4) + "}T\\d)")
  };
  private static final Pattern[] TRACK_REGEX = {Pattern.compile("^D(\\d+)T(\\d+)")};
  private final Map<Class<?>, FileParser<?>> parsers = new HashMap<>();
  private final Map<Class<?>, FileComposer<?>> composers = new HashMap<>();
  private final Map<Class<?>, Map<File, CachedFile<?>>> filesCache =
      new HashMap<>();

  /** Creates a new file service. */
  public FileService() {
    registerParser(ArtworkFile.class, new ArtworkFileParser());
    registerParser(AudioFile.class, new AudioFileParser());
    registerParser(CueSheet.class, new CueSheetParser());
    registerParser(Digest.class, new DigestDao());
    registerParser(Album.class, new AlbumXmlDao());
    registerComposer(Album.class, new AlbumXmlDao());
  }

  /**
   * Gets the parser registered for the given type.
   *
   * @param type the type of parser to retrieve
   * @return the parser
   */
  @SuppressWarnings("unchecked")
  public <T> FileParser<T> getParser(Class<T> type) {
    return (FileParser<T>) parsers.get(type);
  }

  /**
   * Registers a parser for the given class.
   *
   * @param type the type of the parser
   * @param parser the parser to register
   */
  public <T> void registerParser(Class<T> type, FileParser<T> parser) {
    parsers.put(type, parser);
  }

  /**
   * Gets the composer registered for the given type.
   *
   * @param type the type of composer to retrieve
   * @return the composer
   */
  @SuppressWarnings("unchecked")
  public <T> FileComposer<T> getComposer(Class<T> type) {
    return (FileComposer<T>) composers.get(type);
  }

  /**
   * Registers a composer for the given class.
   *
   * @param type the type of the composer
   * @param composer the composer to register
   */
  public <T> void registerComposer(Class<T> type, FileComposer<T> composer) {
    composers.put(type, composer);
  }

  /**
   * Creates a cached file with the parameters.
   *
   * @param type the type of value the file has
   * @param file the file to cache
   * @return the cached file
   */
  @SuppressWarnings("unchecked")
  public <T> CachedFile<T> createCachedFile(Class<T> type, File file) {
    Map<File, CachedFile<?>> tempCache;
    if (filesCache.containsKey(type)) {
      tempCache = filesCache.get(type);
    } else {
      tempCache = new HashMap<>();
      filesCache.put(type, tempCache);
    }

    CachedFile<T> cachedFile;
    if (tempCache.containsKey(file)) {
      cachedFile = (CachedFile<T>) tempCache.get(file);
    } else {
      cachedFile = new CachedFile<>(getParser(type), file);
      tempCache.put(file, cachedFile);
    }
    return cachedFile;
  }

  /**
   * Creates a cached directory with the parameters.
   *
   * @param type the type of value the files have
   * @param dir the directory to cache
   * @return the cached directory
   */
  public <T> CachedDir<T> createCachedDir(Class<T> type, File dir) {
    return new CachedDir<>(this, type, dir);
  }

  /**
   * Creates a cached directory with the parameters.
   *
   * @param type the type of value the files have
   * @param dir the directory to cache
   * @param filter the filter to use when creating the directory
   * @return the cached directory
   */
  public <T> CachedDir<T> createCachedDir(Class<T> type, File dir, FileFilter filter) {
    return new CachedDir<>(this, type, dir, filter);
  }

  /**
   * Creates a disc file.
   *
   * @param file the underlying file
   * @return the disc file
   */
  public DiscFile createDiscFile(File file) {
    if (!isAudioFile(file)) {
      throw new IllegalArgumentException(
          "File (" + file.getName() + ") is not a supported audio file.");
    }
    for (var pattern : DISC_REGEX) {
      var matcher = pattern.matcher(file.getName());
      if (matcher.find()) {
        var discNum = matcher.groupCount() == 0 ? 1 : Integer.valueOf(matcher.group(1));
        var album =
            createCachedFile(Album.class, new File(file.getParentFile(), Album.FILE_NAME))
                .getValue();
        if (album == null) {
          return new DiscFile(file, discNum);
        } else {
          return new DiscFile(file, album.getDisc(discNum));
        }
      }
    }
    throw new IllegalArgumentException(messageForNoMatch(file.getName(), DISC_REGEX));
  }

  /**
   * Creates a disc file.
   *
   * @param source the source file
   * @param newDir the new directory to create the file in
   * @param newFormat the format of the new file
   * @return the disc file
   */
  public DiscFile createDiscFile(DiscFile source, File newDir, AudioFormat newFormat) {
    var newFile = createFile(source, newDir, newFormat);
    if (source.getMeta() == null) {
      return new DiscFile(newFile, source.getDiscNum());
    } else {
      return new DiscFile(newFile, source.getMeta());
    }
  }

  /**
   * Creates a track file.
   *
   * @param file the underlying file
   * @return the track file
   */
  public TrackFile createTrackFile(File file) {
    if (!isAudioFile(file)) {
      throw new IllegalArgumentException(
          "File (" + file.getName() + ") is not a supported audio file.");
    }
    for (var pattern : TRACK_REGEX) {
      var matcher = pattern.matcher(file.getName());
      if (matcher.find()) {
        int discNum = Integer.valueOf(matcher.group(1));
        int trackNum = Integer.valueOf(matcher.group(2));
        var album =
            createCachedFile(Album.class, new File(file.getParentFile(), Album.FILE_NAME))
                .getValue();
        if (album == null) {
          return new TrackFile(file, discNum, trackNum);
        } else {
          return new TrackFile(file, album.getDisc(discNum).getTrack(trackNum));
        }
      }
    }
    throw new IllegalArgumentException(messageForNoMatch(file.getName(), TRACK_REGEX));
  }

  /**
   * Creates a track file.
   *
   * @param source the source file
   * @param newDir the new directory to create the file in
   * @param newFormat the format of the new file
   * @return the track file
   */
  public TrackFile createTrackFile(TrackFile source, File newDir, AudioFormat newFormat) {
    var newFile = createFile(source, newDir, newFormat);
    if (source.getMeta() == null) {
      return new TrackFile(newFile, source.getDiscNum(), source.getTrackNum());
    } else {
      return new TrackFile(newFile, source.getMeta());
    }
  }

  /**
   * Creates an audio file.
   *
   * @param file the underlying file
   * @return the audio file
   */
  public AudioFile createAudioFile(File file) {
    try {
      return createDiscFile(file);
    } catch (IllegalArgumentException e) {
      try {
        return createTrackFile(file);
      } catch (IllegalArgumentException e2) {
        throw new IllegalArgumentException(
            messageForNoMatch(file.getName(), DISC_REGEX, TRACK_REGEX));
      }
    }
  }

  /**
   * Creates an audio file.
   *
   * @param source the source file
   * @param newDir the new directory to create the file in
   * @param newFormat the format of the new file
   * @return the audio file
   */
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

  /**
   * Helper method to create a new file in a different directory.
   *
   * @param source the source file
   * @param newDir the new directory to create the file in
   * @param newFormat the new format of the file
   * @return the new file
   */
  private File createFile(AudioFile source, File newDir, AudioFormat newFormat) {
    var baseName = FileUtil.getBaseName(source.getFile());
    return new File(newDir, baseName + "." + newFormat.getExtension());
  }

  private boolean isAudioFile(File file) {
    for (var format : AudioFormat.values()) {
      if (format.isFormatOf(file)) {
        return true;
      }
    }
    return false;
  }

  private String messageForNoMatch(String fileName, Pattern[]... patternArrays) {
    var builder = new StringBuilder();
    builder.append("File name (");
    builder.append(fileName);
    builder.append(") must match pattern: ");
    var first = true;
    for (var patterns : patternArrays) {
      for (var pattern : patterns) {
        if (!first) {
          builder.append(" or ");
        }
        builder.append(pattern.pattern());
        first = false;
      }
    }
    return builder.toString();
  }

  /**
   * Basic parser of audio files.
   *
   * @author Andrew Oberstar
   * @version 0.1.0
   * @since 0.1.0
   */
  private class AudioFileParser implements FileParser<AudioFile> {
    /**
     * Parses audio files by delegating to {@link FileService#createAudioFile(File)}.
     *
     * @param file the file to parse
     * @return the parsed file
     */
    @Override
    public AudioFile parse(File file) {
      try {
        return createAudioFile(file);
      } catch (IllegalArgumentException e) {
        logger.debug("Invalid file: {}", e.getMessage());
        return null;
      }
    }
  }

  private static class ArtworkFileParser implements FileParser<ArtworkFile> {
    @Override
    public ArtworkFile parse(File file) {
      return isArtworkFile(file) ? new ArtworkFile(file) : null;
    }

    private boolean isArtworkFile(File file) {
      for (var format : ArtworkFormat.values()) {
        if (format.isFormatOf(file)) {
          return true;
        }
      }
      return false;
    }
  }
}
