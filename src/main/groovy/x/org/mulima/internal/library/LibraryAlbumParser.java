package x.org.mulima.internal.library;

import java.io.File;

import x.org.mulima.api.file.FileParser;
import x.org.mulima.api.library.Library;
import x.org.mulima.api.library.LibraryAlbum;

public class LibraryAlbumParser implements FileParser<LibraryAlbum> {
	private final Library lib;
	
	public LibraryAlbumParser(Library lib) {
		this.lib = lib;
	}
	
	@Override
	public LibraryAlbum parse(File file) {
		// TODO Auto-generated method stub
		return null;
	}

}
