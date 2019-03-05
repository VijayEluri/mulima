package org.mulima.file;

import java.io.File;

import org.mulima.library.LibraryAlbum;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a digest service.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class DigestService {
  private DigestDao dao = new DigestDao();

  /**
   * Creates a digest representing the current state of the files in the given library album.
   *
   * @param libAlbum the library album to create a digest for
   * @return a digest representing the current state
   */
  public Digest create(LibraryAlbum libAlbum) {
    return new DigestBuilder(libAlbum).build();
  }

  /**
   * Reads the library album's digest from a file.
   *
   * @param libAlbum the library album
   * @return the digest from the file.
   */
  public Digest read(LibraryAlbum libAlbum) {
    return dao.parse(new File(libAlbum.getDir(), Digest.FILE_NAME));
  }

  /**
   * Reads the library album's source digest from a file.
   *
   * @param libAlbum the library album
   * @return the source digest from the file
   */
  public Digest readSource(LibraryAlbum libAlbum) {
    return dao.parse(new File(libAlbum.getDir(), Digest.SOURCE_FILE_NAME));
  }

  /**
   * Writes the digest and source digest files for the specified album.
   *
   * @param libAlbum the album to write digests for
   * @param source the source of {@code libAlbum}
   */
  public void write(LibraryAlbum libAlbum, LibraryAlbum sourceAlbum) {
    dao.compose(new File(libAlbum.getDir(), Digest.FILE_NAME), create(libAlbum));
    if (sourceAlbum != null) {
      dao.compose(new File(libAlbum.getDir(), Digest.SOURCE_FILE_NAME), create(sourceAlbum));
    }
  }
}
