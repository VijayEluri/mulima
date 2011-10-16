package x.org.mulima.api.library;

import x.org.mulima.api.file.FileParser;

public interface LibraryAlbumFactory extends FileParser<LibraryAlbum> {
	LibraryAlbum create(Library lib, LibraryAlbum source);
}
