package org.mulima.api.library;

import java.io.File;
import java.io.IOException;

public interface LibraryAlbumFactory {
	LibraryAlbum newAlbum(Library lib, LibraryAlbum libAlbum);
	LibraryAlbum newAlbum(Library lib, File dir) throws IOException;
}
