package org.mulima.library;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.exception.UncheckedMulimaException;
import org.mulima.file.CachedDir;
import org.mulima.file.CachedFile;
import org.mulima.file.Digest;
import org.mulima.file.FileService;
import org.mulima.file.audio.ArtworkFile;
import org.mulima.file.audio.AudioFile;
import org.mulima.meta.Album;
import org.mulima.meta.CueSheet;
import org.mulima.meta.GenericTag;
import org.mulima.util.FileUtil;
import org.mulima.util.MetadataUtil;

/**
 * Default implementation of a library album.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class LibraryAlbum implements Comparable<LibraryAlbum> {
  private static final Logger logger = LogManager.getLogger(LibraryAlbum.class);
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
  public LibraryAlbum(FileService fileService, File dir, Library lib) {
    logger.trace("Beginning LibraryAlbum constructor for: {}", dir);
    this.fileService = fileService;
    this.lib = lib;
    setDir(dir);
    logger.trace("Ending LibraryAlbum constructor for: {}", dir);
  }

  /**
   * Gets the ID of this album.
   *
   * @return the ID
   */
  public UUID getId() {
    var dig = getDigest();
    return dig == null ? null : dig.getId();
  }

  /**
   * Gets the ID of this album's source.
   *
   * @return the source ID
   */
  public UUID getSourceId() {
    var dig = getSourceDigest();
    return dig == null ? null : dig.getId();
  }

  /**
   * Gets a human readable name for this album.
   *
   * @return the name
   */
  public String getName() {
    if (getAlbum() == null) {
      return FileUtil.getSafeCanonicalPath(getDir());
    } else {
      var album =
          getAlbum().isSet(GenericTag.ALBUM)
              ? getAlbum().getFlat(GenericTag.ALBUM)
              : MetadataUtil.commonValueFlat(getAlbum().getDiscs(), GenericTag.ALBUM);
      return getAlbum().getFlat(GenericTag.ARTIST) + " - " + album;
    }
  }

  /**
   * Gets the directory this album is stored in.
   *
   * @return the directory
   */
  public File getDir() {
    return dir;
  }

  /**
   * Sets the directory this album is stored in. If the dir was already set, this will rename the
   * existing directory.
   *
   * @param dir the new directory
   */
  public void setDir(File dir) {
    if (this.dir != null) {
      if (this.dir.equals(dir)) {
        return;
      }
      if (!this.dir.renameTo(dir)) {
        throw new UncheckedMulimaException("Failed to rename " + this.dir + " to " + dir);
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
            pathname -> pathname.getName().endsWith(".cue"));
    this.artwork = fileService.createCachedDir(ArtworkFile.class, dir);
  }

  /**
   * Gets the library this album is stored in.
   *
   * @return the library
   */
  public Library getLib() {
    return lib;
  }

  /**
   * Gets the album metadata that goes with this album.
   *
   * @return the metadata
   */
  public Album getAlbum() {
    return album.getValue();
  }

  /**
   * Gets the audio files for this album.
   *
   * @return the audio files
   */
  public Set<AudioFile> getAudioFiles() {
    return audioFiles.getValues();
  }

  /**
   * Gets the cue sheets for this album.
   *
   * @return the cue sheets
   */
  public Set<CueSheet> getCueSheets() {
    return cueSheets.getValues();
  }

  /**
   * Gets the artwork files for this album.
   *
   * @return the artwork
   */
  public Set<ArtworkFile> getArtwork() {
    return artwork.getValues();
  }

  /**
   * Gets a digest representing the state of this album the last time it was updated.
   *
   * @return the digest
   */
  public Digest getDigest() {
    return digest.getValue();
  }

  /**
   * Gets a digest representing the state of the source album the last time this album was updated.
   *
   * @return the source digest
   */
  public Digest getSourceDigest() {
    return sourceDigest.getValue();
  }

  /** Cleans up all files (except digest files), generally in preparation for a new conversion. */
  public void cleanDir() {
    for (var file : getDir().listFiles()) {
      if (Digest.FILE_NAME.equals(file.getName())
          || Digest.SOURCE_FILE_NAME.equals(file.getName())) {
      } else if (!file.delete()) {
        throw new UncheckedMulimaException("Could not delete file: " + file);
      }
    }
  }

  @Override
  public int compareTo(LibraryAlbum o) {
    var thisName = getName();
    var oName = o.getName();
    return thisName.compareToIgnoreCase(oName);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof LibraryAlbum) {
      var that = (LibraryAlbum) obj;
      if (this.getId() == null && that.getId() == null) {
        return this == that;
      } else {
        return Objects.equals(this.getId(), that.getId());
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
