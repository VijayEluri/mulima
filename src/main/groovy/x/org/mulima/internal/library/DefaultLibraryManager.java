package x.org.mulima.internal.library;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.org.mulima.api.MulimaService;
import x.org.mulima.api.library.Library;
import x.org.mulima.api.library.LibraryAlbum;
import x.org.mulima.api.library.LibraryManager;
import x.org.mulima.api.library.ReferenceLibrary;

public class DefaultLibraryManager implements LibraryManager {
	private static final Logger logger = LoggerFactory.getLogger(DefaultLibraryManager.class);
	private final MulimaService service;
	
	public DefaultLibraryManager(MulimaService service) {
		this.service = service;
	}
	
	@Override
	public void updateAll() {
		update(service.getDestLibs());
	}
	
	@Override
	public void update(Library lib) {
		if (!service.getDestLibs().contains(lib)) {
			throw new IllegalArgumentException("Cannot update a library that doesn't belong to this manager.");
		}
		Set<Library> libs = new HashSet<Library>();
		libs.add(lib);
		update(libs);
	}
	
	@Override
	public void update(Set<Library> lib) {
		Set<LibraryAlbum> refAlbums = new HashSet<LibraryAlbum>();
		for (ReferenceLibrary refLib : service.getRefLibs()) {
			refAlbums.addAll(refLib.getAll());
		}
		
		for (LibraryAlbum refAlbum : refAlbums) {
			Set<LibraryAlbum> destAlbums = new HashSet<LibraryAlbum>();
			for (Library destLib : service.getDestLibs()) {
				destAlbums.add(destLib.getSourcedFrom(refAlbum));
			}
			service.getConversionService().submit(refAlbum, destAlbums);
		}
	}
}
