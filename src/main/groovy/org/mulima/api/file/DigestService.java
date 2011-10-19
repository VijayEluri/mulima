package org.mulima.api.file;

import org.mulima.api.library.LibraryAlbum;

public interface DigestService {
	Digest create(LibraryAlbum libAlbum);
	Digest read(LibraryAlbum libAlbum);
	Digest readSource(LibraryAlbum libAlbum);
	void write(LibraryAlbum libAlbum, LibraryAlbum source);
}