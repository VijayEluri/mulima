/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mulima.internal.library;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.GenericTag;
import org.mulima.exception.UncheckedIOException;
import org.mulima.exception.UncheckedMulimaException;
import org.mulima.internal.file.LeafDirFilter;
import org.mulima.util.FileUtil;
import org.mulima.util.MetadataUtil;
import org.mulima.util.StringUtil;

/**
 * Default implementation of a library.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DefaultLibrary implements Library {
  private static final Logger logger = LoggerFactory.getLogger(DefaultLibrary.class);
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
  public DefaultLibrary(
      LibraryAlbumFactory libAlbumFactory, String name, File rootDir, AudioFormat format) {
    this.libAlbumFactory = libAlbumFactory;
    this.name = name;
    this.rootDir = rootDir;
    this.format = format;
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return name;
  }

  /** {@inheritDoc} */
  @Override
  public File getRootDir() {
    return rootDir;
  }

  /** {@inheritDoc} */
  @Override
  public AudioFormat getFormat() {
    return format;
  }

  /** {@inheritDoc} */
  @Override
  public Set<LibraryAlbum> getAll() {
    if (albums == null) {
      scanAll();
    }
    return albums;
  }

  /** {@inheritDoc} */
  @Override
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

  /** {@inheritDoc} */
  @Override
  public LibraryAlbum getSourcedFrom(LibraryAlbum source) {
    return getSourcedFrom(source, true);
  }

  /** {@inheritDoc} */
  @Override
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

  /** {@inheritDoc} */
  @Override
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
        throw new UncheckedIOException("Could not create album directory: " + dir);
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
