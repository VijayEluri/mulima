package org.mulima.api.library;

import org.mulima.api.file.FileParser;

public interface LibraryAlbumFactory extends FileParser<LibraryAlbum> {
	LibraryAlbum create(Library lib, LibraryAlbum source);
}
