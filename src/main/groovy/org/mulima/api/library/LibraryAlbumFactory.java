package org.mulima.api.library;

import java.io.File;

/**
 * Factory class for creating library albums.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface LibraryAlbumFactory {
	/**
	 * Creates a library album using the parameters.
	 * @param dir the directory of the album
	 * @param lib the library the album belongs in
	 * @return a new library album
	 */
	LibraryAlbum create(File dir, Library lib);
}
