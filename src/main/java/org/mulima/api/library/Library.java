package org.mulima.api.library;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.meta.Album;

/**
 * An object representing a related collection of music. This is often used to group music by target
 * device.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface Library {
  /**
   * Gets the name of the library.
   *
   * @return the name
   */
  String getName();

  /**
   * Gets the root directory of the library.
   *
   * @return the root directory
   */
  File getRootDir();

  /**
   * Gets the audio format of the files in this library.
   *
   * @return the audio format
   */
  AudioFormat getFormat();

  /**
   * Gets all library albums in this library.
   *
   * @return a set of all albums
   */
  Set<LibraryAlbum> getAll();

  /**
   * Gets the library album in this library that has the specified ID. This will return null if the
   * album could not be found.
   *
   * @param id the ID to search for
   * @return the album with the ID, or {@code null}
   */
  LibraryAlbum getById(UUID id);

  /**
   * Gets the library album in this library that was sourced from the parameter. If no album in this
   * library was sourced from the parameter, a new one will be created.
   *
   * @param source the source album
   * @return an album sourced from {@code source}
   */
  LibraryAlbum getSourcedFrom(LibraryAlbum source);

  /**
   * Gets the library album in this library that was sourced from the parameter. If no album in this
   * library was sourced from the parameter, a new one will only be created if {@code
   * createIfNotFound} is {@code true}. Otherwise {@code null} will be returned.
   *
   * @param source the source album
   * @param createIfNotFound {@code true} if a new album should be created, if one is not found.
   *        {@code false} otherwise
   * @return an album source from {@code source}
   */
  LibraryAlbum getSourcedFrom(LibraryAlbum source, boolean createIfNotFound);

  /**
   * Determine the directory within this library that a library album for the given metadata should
   * go.
   *
   * @param meta the metadata to determine the dir for
   * @return the directory a library album should be in
   */
  File determineDir(Album meta);
}
