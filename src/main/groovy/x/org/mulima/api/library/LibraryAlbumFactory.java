package x.org.mulima.api.library;

import java.io.File;


public interface LibraryAlbumFactory {
	LibraryAlbum create(Library lib, LibraryAlbum source);
	LibraryAlbum create(Library lib, File dir);
}
