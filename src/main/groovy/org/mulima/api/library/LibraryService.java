package org.mulima.api.library;

import java.io.File;
import java.util.Set;
import java.util.UUID;

/**
 * A service that provides information
 * about the libraries currently configured.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface LibraryService {
	/**
	 * Gets all reference libraries.
	 * @return the reference libraries
	 */
	Set<ReferenceLibrary> getRefLibs();
	
	/**
	 * Gets all destination libraries.
	 * @return the destination libraries
	 */
	Set<Library> getDestLibs();
	
	/**
	 * Gets the library that the specified
	 * directory belongs to.
	 * @param dir the directory to search for
	 * @return the library that {@code dir} belongs
	 * to or {@code null} if one can't be found
	 */
	Library getLibFor(File dir);
	
	/**
	 * Looks in all libraries for one matching
	 * the specified ID.
	 * @param id the ID of the album to find
	 * @return the album, or {@code null} if one can't
	 * be found
	 */
	LibraryAlbum getAlbumById(UUID id);
	
	/**
	 * Checks if the specified library is up to date.  Will check
	 * to see if the source files have changed if {@code checkSource}
	 * is set to {@code true}.
	 * @param libAlbum the album to check
	 * @param checkSource whether or not to check the album's source
	 * as well
	 * @return {@code true} if the album is up to date, {@code false}
	 * otherwise
	 */
	boolean isUpToDate(LibraryAlbum libAlbum, boolean checkSource);
}
