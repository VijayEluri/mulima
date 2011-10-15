package x.org.mulima.api.file;

import x.org.mulima.api.library.LibraryAlbum;

public interface DigestService {
	Digest build(LibraryAlbum libAlbum);
	Digest read(LibraryAlbum libAlbum);
	Digest readSource(LibraryAlbum libAlbum);
	void write(LibraryAlbum libAlbum);
}
