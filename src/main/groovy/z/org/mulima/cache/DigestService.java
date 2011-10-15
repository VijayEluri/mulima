package z.org.mulima.cache;

import z.org.mulima.api.library.LibraryAlbum;

public interface DigestService {
	Digest buildDigest(LibraryAlbum libAlbum);
	Digest readDigest(LibraryAlbum libAlbum);
	Digest readSourceDigest(LibraryAlbum libAlbum);
	void readDigests(LibraryAlbum libAlbum);
	void writeDigests(LibraryAlbum libAlbum);
}
