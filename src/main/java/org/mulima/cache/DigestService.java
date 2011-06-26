package org.mulima.cache;

import java.io.IOException;

import org.mulima.api.library.LibraryAlbum;

public interface DigestService {
	Digest buildDigest(LibraryAlbum libAlbum) throws IOException;
	void readDigests(LibraryAlbum libAlbum) throws IOException;
	void writeDigests(LibraryAlbum libAlbum) throws IOException;
}
