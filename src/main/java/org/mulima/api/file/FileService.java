package org.mulima.api.file;

import java.io.File;
import java.io.FileFilter;

import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.file.audio.DiscFile;
import org.mulima.api.file.audio.TrackFile;

/**
 * A service that provides operations that create file objects.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface FileService {
  /**
   * Gets the parser registered for the given type.
   *
   * @param type the type of parser to retrieve
   * @return the parser
   */
  <T> FileParser<T> getParser(Class<T> type);

  /**
   * Gets the composer registered for the given type.
   *
   * @param type the type of composer to retrieve
   * @return the composer
   */
  <T> FileComposer<T> getComposer(Class<T> type);

  /**
   * Creates a cached file with the parameters.
   *
   * @param type the type of value the file has
   * @param file the file to cache
   * @return the cached file
   */
  <T> CachedFile<T> createCachedFile(Class<T> type, File file);

  /**
   * Creates a cached directory with the parameters.
   *
   * @param type the type of value the files have
   * @param dir the directory to cache
   * @return the cached directory
   */
  <T> CachedDir<T> createCachedDir(Class<T> type, File dir);

  /**
   * Creates a cached directory with the parameters.
   *
   * @param type the type of value the files have
   * @param dir the directory to cache
   * @param filter the filter to use when creating the directory
   * @return the cached directory
   */
  <T> CachedDir<T> createCachedDir(Class<T> type, File dir, FileFilter filter);

  /**
   * Creates a disc file.
   *
   * @param file the underlying file
   * @return the disc file
   */
  DiscFile createDiscFile(File file);

  /**
   * Creates a disc file.
   *
   * @param source the source file
   * @param newDir the new directory to create the file in
   * @param newFormat the format of the new file
   * @return the disc file
   */
  DiscFile createDiscFile(DiscFile source, File newDir, AudioFormat newFormat);

  /**
   * Creates a track file.
   *
   * @param file the underlying file
   * @return the track file
   */
  TrackFile createTrackFile(File file);

  /**
   * Creates a track file.
   *
   * @param source the source file
   * @param newDir the new directory to create the file in
   * @param newFormat the format of the new file
   * @return the track file
   */
  TrackFile createTrackFile(TrackFile source, File newDir, AudioFormat newFormat);

  /**
   * Creates an audio file.
   *
   * @param file the underlying file
   * @return the audio file
   */
  AudioFile createAudioFile(File file);

  /**
   * Creates an audio file.
   *
   * @param source the source file
   * @param newDir the new directory to create the file in
   * @param newFormat the format of the new file
   * @return the audio file
   */
  AudioFile createAudioFile(AudioFile source, File newDir, AudioFormat newFormat);
}
