package x.org.mulima.api.library;

import java.io.File;
import java.util.Set;

import x.org.mulima.api.audio.AudioFormat;

public interface Library {
	String getName();
	File getRootDir();
	AudioFormat getFormat();
	Set<LibraryAlbum> getAll();
	LibraryAlbum getSourcedFrom(LibraryAlbum source);
	LibraryAlbum getSourcedFrom(LibraryAlbum source, boolean createIfNotFound);
}
