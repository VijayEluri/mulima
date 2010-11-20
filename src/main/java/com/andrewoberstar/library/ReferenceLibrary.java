package com.andrewoberstar.library;

import java.util.List;

import com.andrewoberstar.library.meta.Disc;
import com.andrewoberstar.library.ui.UICallback;

public interface ReferenceLibrary extends Library {
	void findAlbums();
	List<AlbumFolder> getAllAlbums();
	List<AlbumFolder> getNewAlbums();
	void processNewAlbums(UICallback<Disc> callback);
}
