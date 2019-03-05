package org.mulima.library;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.exception.UncheckedMulimaException;
import org.mulima.file.Digest;
import org.mulima.file.DigestService;
import org.mulima.file.audio.AudioFormat;
import org.mulima.service.MulimaProperties;
import org.mulima.service.MulimaPropertiesSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a library service.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class LibraryService extends MulimaPropertiesSupport {
  private static final Logger logger = LogManager.getLogger(LibraryService.class);
  private LibraryAlbumFactory libAlbumFactory;
  private final DigestService digestService;
  private Set<ReferenceLibrary> refLibs;
  private Set<Library> destLibs;

  /**
   * Constructs a library service from the parameters.
   *
   * @param libAlbumFactory the library album factory to pass to libraries
   * @param digestService the service to use when calculating digests
   * @param refLibs the reference libraries
   * @param destLibs the destination libraries
   */
  @Autowired
  public LibraryService(LibraryAlbumFactory libAlbumFactory, DigestService digestService) {
    this.libAlbumFactory = libAlbumFactory;
    this.digestService = digestService;
  }

  /**
   * Gets all reference libraries.
   *
   * @return the reference libraries
   */
  public Set<ReferenceLibrary> getRefLibs() {
    if (refLibs == null) {
      this.refLibs = createLibs(ReferenceLibrary.class, getProperties().withScope("reflib"));
    }
    return refLibs;
  }

  public void setRefLibs(Set<ReferenceLibrary> refLibs) {
    if (this.refLibs != null) {
      throw new IllegalStateException("Cannot change refernce libraries after they have been set.");
    }
    this.refLibs = Collections.unmodifiableSet(refLibs);
  }

  /**
   * Gets all destination libraries.
   *
   * @return the destination libraries
   */
  public Set<Library> getDestLibs() {
    if (destLibs == null) {
      this.destLibs = createLibs(Library.class, getProperties().withScope("lib"));
    }
    return destLibs;
  }

  public void setDestLibs(Set<Library> destLibs) {
    if (this.destLibs != null) {
      throw new IllegalStateException(
          "Cannot change destination libraries after they have been set.");
    }
    this.destLibs = Collections.unmodifiableSet(destLibs);
  }

  /**
   * Gets the library that the specified directory belongs to.
   *
   * @param dir the directory to search for
   * @return the library that {@code dir} belongs to or {@code null} if one can't be found
   */
  public Library getLibFor(File dir) {
    Set<Library> allLibs = new HashSet<>();
    allLibs.addAll(getRefLibs());
    allLibs.addAll(getDestLibs());

    for (var lib : allLibs) {
      var rootDir = lib.getRootDir();
      var temp = dir;
      while (temp.getParentFile() != null) {
        temp = temp.getParentFile();
        if (temp.equals(rootDir)) {
          return lib;
        }
      }
    }
    return null;
  }

  /**
   * Looks in all libraries for one matching the specified ID.
   *
   * @param id the ID of the album to find
   * @return the album, or {@code null} if one can't be found
   */
  public LibraryAlbum getAlbumById(UUID id) {
    Set<Library> allLibs = new HashSet<>();
    allLibs.addAll(getRefLibs());
    allLibs.addAll(getDestLibs());

    for (var lib : allLibs) {
      var album = lib.getById(id);
      if (album != null) {
        return album;
      }
    }
    return null;
  }

  /**
   * Checks if the specified library is up to date. Will check to see if the source files have changed
   * if {@code checkSource} is set to {@code true}.
   *
   * @param libAlbum the album to check
   * @param checkSource whether or not to check the album's source as well
   * @return {@code true} if the album is up to date, {@code false} otherwise
   */
  public boolean isUpToDate(LibraryAlbum libAlbum, boolean checkSource) {
    var digest = libAlbum.getDigest();
    if (digest == null) {
      return false;
    } else if (checkSource && !isSourceUpToDate(libAlbum)) {
      return false;
    }
    return isUpToDate(libAlbum, digest);
  }

  /**
   * Checks if the source of the specified album is up to date.
   *
   * @param libAlbum the album to check
   * @return {@code true} if the source is up to date, {@code false} otherwise
   */
  private boolean isSourceUpToDate(LibraryAlbum libAlbum) {
    var sourceDigest = libAlbum.getSourceDigest();
    if (sourceDigest == null) {
      return true;
    }
    var source = getAlbumById(sourceDigest.getId());
    if (source == null) {
      throw new UncheckedMulimaException(
          "Source album for " + libAlbum.getId() + " not found: " + sourceDigest.getId());
    } else {
      return isUpToDate(source, sourceDigest);
    }
  }

  /**
   * Checks if an album is up to date compared to the cached digest.
   *
   * @param album the album to check
   * @param digest the digest representing a previous state
   * @return {@code true} if up to date, {@code false} otherwise
   */
  private boolean isUpToDate(LibraryAlbum album, Digest cached) {
    logger.trace("Beginning isUpToDate for {} ({})", album, album.getLib());
    if (cached == null) {
      return false;
    }
    var current = digestService.create(album);
    return cached.equals(current);
  }

  @SuppressWarnings("unchecked")
  private <T> Set<T> createLibs(Class<T> type, MulimaProperties props) {
    Set<T> libs = new HashSet<>();
    var names = props.getSubScopes();
    for (var name : names) {
      var namedProps = props.withScope(name);
      var dir = new File(namedProps.getProperty("dir"));
      var format = AudioFormat.valueOf(namedProps.getProperty("format"));
      if (type.isAssignableFrom(ReferenceLibrary.class)) {
        libs.add((T) new ReferenceLibrary(libAlbumFactory, name, dir, format));
      } else {
        libs.add((T) new Library(libAlbumFactory, name, dir, format));
      }
    }
    return libs;
  }

  @Override
  protected List<String> getScope() {
    return Collections.emptyList();
  }
}
