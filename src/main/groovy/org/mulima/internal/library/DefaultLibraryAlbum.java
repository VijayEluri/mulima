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
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mulima.api.file.CachedDir;
import org.mulima.api.file.CachedFile;
import org.mulima.api.file.Digest;
import org.mulima.api.file.FileService;
import org.mulima.api.file.audio.ArtworkFile;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.GenericTag;
import org.mulima.exception.UncheckedIOException;
import org.mulima.util.FileUtil;
import org.mulima.util.MetadataUtil;
import org.mulima.util.ObjectUtil;

/**
 * Default implementation of a library album.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DefaultLibraryAlbum implements LibraryAlbum {
  private static final Logger logger = LoggerFactory.getLogger(DefaultLibraryAlbum.class);
  private final FileService fileService;
  private final Library lib;
  private File dir;
  private CachedFile<Album> album;
  private CachedFile<Digest> digest;
  private CachedFile<Digest> sourceDigest;
  private CachedDir<AudioFile> audioFiles;
  private CachedDir<CueSheet> cueSheets;
  private CachedDir<ArtworkFile> artwork;

  /**
   * Constructs a library album from the parameters.
   *
   * @param fileService the service to use when finding files
   * @param dir the directory where this album's files reside
   * @param lib the library this album is contained in
   */
  public DefaultLibraryAlbum(FileService fileService, File dir, Library lib) {
    logger.trace("Beginning LibraryAlbum constructor for: {}", dir);
    this.fileService = fileService;
    this.lib = lib;
    setDir(dir);
    logger.trace("Ending LibraryAlbum constructor for: {}", dir);
  }

  /** {@inheritDoc} */
  @Override
  public UUID getId() {
    Digest dig = getDigest();
    return dig == null ? null : dig.getId();
  }

  /** {@inheritDoc} */
  @Override
  public UUID getSourceId() {
    Digest dig = getSourceDigest();
    return dig == null ? null : dig.getId();
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    if (getAlbum() == null) {
      return FileUtil.getSafeCanonicalPath(getDir());
    } else {
      String album =
          getAlbum().isSet(GenericTag.ALBUM)
              ? getAlbum().getFlat(GenericTag.ALBUM)
              : MetadataUtil.commonValueFlat(getAlbum().getDiscs(), GenericTag.ALBUM);
      return getAlbum().getFlat(GenericTag.ARTIST) + " - " + album;
    }
  }

  /** {@inheritDoc} */
  @Override
  public File getDir() {
    return dir;
  }

  /** {@inheritDoc} */
  @Override
  public void setDir(File dir) {
    if (this.dir != null) {
      if (this.dir.equals(dir)) {
        return;
      }
      if (!this.dir.renameTo(dir)) {
        throw new UncheckedIOException("Failed to rename " + this.dir + " to " + dir);
      }
    }

    this.dir = dir;
    this.album = fileService.createCachedFile(Album.class, new File(dir, "album.xml"));
    this.digest = fileService.createCachedFile(Digest.class, new File(dir, Digest.FILE_NAME));
    this.sourceDigest =
        fileService.createCachedFile(Digest.class, new File(dir, Digest.SOURCE_FILE_NAME));
    this.audioFiles = fileService.createCachedDir(AudioFile.class, dir);
    this.cueSheets =
        fileService.createCachedDir(
            CueSheet.class,
            dir,
            new FileFilter() {
              @Override
              public boolean accept(File pathname) {
                return pathname.getName().endsWith(".cue");
              }
            });
    this.artwork = fileService.createCachedDir(ArtworkFile.class, dir);
  }

  /** {@inheritDoc} */
  @Override
  public Library getLib() {
    return lib;
  }

  /** {@inheritDoc} */
  @Override
  public Album getAlbum() {
    return album.getValue();
  }

  /** {@inheritDoc} */
  @Override
  public Set<AudioFile> getAudioFiles() {
    return audioFiles.getValues();
  }

  /** {@inheritDoc} */
  @Override
  public Set<CueSheet> getCueSheets() {
    return cueSheets.getValues();
  }

  /** {@inheritDoc} */
  @Override
  public Set<ArtworkFile> getArtwork() {
    return artwork.getValues();
  }

  /** {@inheritDoc} */
  @Override
  public Digest getDigest() {
    return digest.getValue();
  }

  /** {@inheritDoc} */
  @Override
  public Digest getSourceDigest() {
    return sourceDigest.getValue();
  }

  /** {@inheritDoc} */
  @Override
  public void cleanDir() {
    for (File file : getDir().listFiles()) {
      if (Digest.FILE_NAME.equals(file.getName())
          || Digest.SOURCE_FILE_NAME.equals(file.getName())) {
        continue;
      } else if (!file.delete()) {
        throw new UncheckedIOException("Could not delete file: " + file);
      }
    }
  }

  @Override
  public int compareTo(LibraryAlbum o) {
    String thisName = getName();
    String oName = o.getName();
    return thisName.compareToIgnoreCase(oName);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof DefaultLibraryAlbum) {
      LibraryAlbum that = (LibraryAlbum) obj;
      if (this.getId() == null && that.getId() == null) {
        return this == that;
      } else {
        return ObjectUtil.isEqual(this.getId(), that.getId());
      }
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return getId() == null ? System.identityHashCode(this) : getId().hashCode();
  }

  @Override
  public String toString() {
    return getName();
  }
}
