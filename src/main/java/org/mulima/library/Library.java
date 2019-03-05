package org.mulima.library;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.exception.UncheckedMulimaException;
import org.mulima.file.LeafDirFilter;
import org.mulima.file.audio.AudioFormat;
import org.mulima.meta.Album;
import org.mulima.meta.GenericTag;
import org.mulima.util.FileUtil;
import org.mulima.util.MetadataUtil;
import org.mulima.util.StringUtil;

/**
 * Default implementation of a library.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class Library {
  private static final Logger logger = LogManager.getLogger(Library.class);
  private final LibraryAlbumFactory libAlbumFactory;
  private final String name;
  private final File rootDir;
  private final AudioFormat format;
  private Set<LibraryAlbum> albums = null;

  /**
   * Constructs a library from the parameters.
   *
   * @param libAlbumFactory the factory to create library albums
   * @param name the name of this library
   * @param rootDir the root directory of this library
   * @param format the audio format of the files in this library
   */
  public Library(
      LibraryAlbumFactory libAlbumFactory, String name, File rootDir, AudioFormat format) {
    this.libAlbumFactory = libAlbumFactory;
    this.name = name;
    this.rootDir = rootDir;
    this.format = format;
  }

  /**
   * Gets the name of the library.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the root directory of the library.
   *
   * @return the root directory
   */
  public File getRootDir() {
    return rootDir;
  }

  /**
   * Gets the audio format of the files in this library.
   *
   * @return the audio format
   */
  public AudioFormat getFormat() {
    return format;
  }

  /**
   * Gets all library albums in this library.
   *
   * @return a set of all albums
   */
  public Set<LibraryAlbum> getAll() {
    if (albums == null) {
      scanAll();
    }
    return albums;
  }

  /**
   * Gets the library album in this library that has the specified ID. This will return null if the
   * album could not be found.
   *
   * @param id the ID to search for
   * @return the album with the ID, or {@code null}
   */
  public LibraryAlbum getById(UUID id) {
    if (id == null) {
      return null;
    }
    for (LibraryAlbum album : getAll()) {
      if (id.equals(album.getId())) {
        return album;
      }
    }
    return null;
  }

  /**
   * Gets the library album in this library that was sourced from the parameter. If no album in this
   * library was sourced from the parameter, a new one will be created.
   *
   * @param source the source album
   * @return an album sourced from {@code source}
   */
  public LibraryAlbum getSourcedFrom(LibraryAlbum source) {
    return getSourcedFrom(source, true);
  }

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
  public LibraryAlbum getSourcedFrom(LibraryAlbum source, boolean createIfNotFound) {
    if (source == null) {
      throw new IllegalArgumentException("Source must not be null.");
    }
    for (LibraryAlbum album : getAll()) {
      if (source.getId().equals(album.getSourceId())) {
        return album;
      }
    }
    return createIfNotFound ? createAlbum(source) : null;
  }

  /**
   * Determine the directory within this library that a library album for the given metadata should
   * go.
   *
   * @param meta the metadata to determine the dir for
   * @return the directory a library album should be in
   */
  public File determineDir(Album meta) {
    String album;
    if (meta.isSet(GenericTag.ALBUM)) {
      album = meta.getFlat(GenericTag.ALBUM);
    } else {
      album = MetadataUtil.commonValueFlat(meta.getDiscs(), GenericTag.ALBUM);
    }
    if (album == null || !meta.isSet(GenericTag.ARTIST)) {
      throw new UncheckedMulimaException(
          "Could not determine directory due to missing ALBUM or ARTIST tag.");
    }
    String relPath =
        StringUtil.makeSafe(meta.getFlat(GenericTag.ARTIST)).trim()
            + File.separator
            + StringUtil.makeSafe(album).trim();
    return new File(getRootDir(), relPath);
  }

  /** Scans all directories under the root directory for library albums. */
  private void scanAll() {
    logger.trace("Beginning scanAll for {}", getName());
    this.albums = new TreeSet<LibraryAlbum>();
    FileFilter filter = new LeafDirFilter();
    for (File dir : FileUtil.listDirsRecursive(getRootDir())) {
      if (filter.accept(dir)) {
        albums.add(libAlbumFactory.create(dir, this));
      }
    }
    logger.trace("Ending scanAll for {}", getName());
  }

  /**
   * Creates a new library album from the source.
   *
   * @param source the source album
   * @return the new album
   */
  private LibraryAlbum createAlbum(LibraryAlbum source) {
    try {
      File dir = determineDir(source.getAlbum());
      if (!dir.exists() && !dir.mkdirs()) {
        throw new UncheckedMulimaException("Could not create album directory: " + dir);
      }
      return libAlbumFactory.create(dir, this);
    } catch (UncheckedMulimaException e) {
      throw new UncheckedMulimaException(
          "Could not determine directory from source album: " + source.getDir(), e);
    }
  }

  @Override
  public String toString() {
    return getName();
  }
}
