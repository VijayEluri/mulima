package org.mulima.internal.library;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.file.FileService;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.ReferenceLibrary;

public class DefaultReferenceLibrary extends DefaultLibrary implements ReferenceLibrary {
	public DefaultReferenceLibrary(FileService fileService, String name, File rootDir, AudioFormat format) {
		super(fileService, name, rootDir, format);
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
