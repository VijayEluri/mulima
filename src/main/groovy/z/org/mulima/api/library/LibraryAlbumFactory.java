package z.org.mulima.api.library;

import java.io.File;

public interface LibraryAlbumFactory {
	<T extends LibraryAlbum> T newAlbum(Library<T> lib, T source);
	<T extends LibraryAlbum> T newAlbum(Library<T> lib, File dir);
}
