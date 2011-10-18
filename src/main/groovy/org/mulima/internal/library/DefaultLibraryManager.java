package org.mulima.internal.library;

import java.util.HashSet;
import java.util.Set;

import org.mulima.api.job.AlbumConversionService;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryManager;
import org.mulima.api.library.LibraryService;
import org.mulima.api.library.ReferenceLibrary;

public class DefaultLibraryManager implements LibraryManager {
	private final LibraryService libraryService;
	private final AlbumConversionService conversionService;
	
	public DefaultLibraryManager(LibraryService libraryService, AlbumConversionService conversionService) {
		this.libraryService = libraryService;
		this.conversionService = conversionService;
	}
	
	
	@Override
	public void processNew() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Implement this");
	}
	
	@Override
	public void updateAll() {
		update(libraryService.getDestLibs());
	}
	
	@Override
	public void update(Library lib) {
		if (!libraryService.getDestLibs().contains(lib)) {
			throw new IllegalArgumentException("Cannot update a library that doesn't belong to this manager.");
		}
		Set<Library> libs = new HashSet<Library>();
		libs.add(lib);
		update(libs);
	}
	
	@Override
	public void update(Set<Library> lib) {
		Set<LibraryAlbum> refAlbums = new HashSet<LibraryAlbum>();
		for (ReferenceLibrary refLib : libraryService.getRefLibs()) {
			refAlbums.addAll(refLib.getAll());
		}
		
		for (LibraryAlbum refAlbum : refAlbums) {
			Set<LibraryAlbum> destAlbums = new HashSet<LibraryAlbum>();
			for (Library destLib : libraryService.getDestLibs()) {
				destAlbums.add(destLib.getSourcedFrom(refAlbum));
			}
			conversionService.submit(refAlbum, destAlbums);
		}
	}
}
