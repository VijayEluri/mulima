package org.mulima.api.job;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.mulima.api.library.LibraryAlbum;


public interface AlbumConversionService {
	Future<Void> submit(LibraryAlbum source, Set<LibraryAlbum> dests);
	void shutdown();
	List<Runnable> shutdownNow();
}
