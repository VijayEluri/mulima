package org.mulima.api.library;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public interface LibraryService {
	Set<ReferenceLibrary> getRefLibs();
	Set<Library> getDestLibs();
	Library getLibFor(File dir);
	LibraryAlbum getAlbumById(UUID id);
	boolean isUpToDate(LibraryAlbum libAlbum, boolean checkSource);
}
