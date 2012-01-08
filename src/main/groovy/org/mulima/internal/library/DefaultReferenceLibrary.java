package org.mulima.internal.library;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.mulima.api.library.ReferenceLibrary;

/**
 * Default implementation of a reference library.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultReferenceLibrary extends DefaultLibrary implements ReferenceLibrary {
	/**
	 * Constructs a reference library from the parameters.
	 * @param libAlbumFactory the factory to create library albums
	 * @param name the name of this library
	 * @param rootDir the root directory of this library
	 * @param format the audio format of the files in this library
	 */
	public DefaultReferenceLibrary(LibraryAlbumFactory libAlbumFactory, String name, File rootDir, AudioFormat format) {
		super(libAlbumFactory, name, rootDir, format);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<LibraryAlbum> getNew() {
		Set<LibraryAlbum> newAlbums = new HashSet<LibraryAlbum>();
		for (LibraryAlbum libAlbum : getAll()) {
			if (libAlbum.getId() == null) {
				newAlbums.add(libAlbum);
			}
		}
		return Collections.unmodifiableSet(newAlbums);
	}

}
