package org.mulima.internal.library;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.mulima.api.library.ReferenceLibrary;

public class DefaultReferenceLibrary extends DefaultLibrary implements ReferenceLibrary {
	public DefaultReferenceLibrary(LibraryAlbumFactory libAlbumFactory, String name, File rootDir, AudioFormat format) {
		super(libAlbumFactory, name, rootDir, format);
	}

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
