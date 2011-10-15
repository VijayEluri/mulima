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
	private final Set<ReferenceLibrary> refLibs;
	private final Set<Library> destLibs;
	
	public DefaultLibraryManager(MulimaService service, Set<ReferenceLibrary> refLibs, Set<Library> destLibs) {
		this.service = service;
		this.refLibs = refLibs;
		this.destLibs = destLibs;
	}
	
	@Override
	public Set<ReferenceLibrary> getRefLibs() {
		return refLibs;
	}

	@Override
	public Set<Library> getDestLibs() {
		return destLibs;
	}
	
	public void updateAll() {
		update(destLibs);
	}
	
	public void update(Library lib) {
		if (!destLibs.contains(lib)) {
			throw new IllegalArgumentException("Cannot update a library that doesn't belong to this manager.");
		}
		Set<Library> libs = new HashSet<Library>();
		libs.add(lib);
		update(libs);
	}
	
	public void update(Set<Library> lib) {
		Set<LibraryAlbum> refAlbums = new HashSet<LibraryAlbum>();
		for (ReferenceLibrary refLib : getRefLibs()) {
			refAlbums.addAll(refLib.getAll());
		}
		
		for (LibraryAlbum refAlbum : refAlbums) {
			Set<LibraryAlbum> destAlbums = new HashSet<LibraryAlbum>();
			for (Library destLib : destLibs) {
				destAlbums.add(destLib.getSourcedFrom(refAlbum));
			}
			service.getConversionService().submit(refAlbum, destAlbums);
		}
	}
}
