package x.org.mulima.internal.library;

import java.io.File;
import java.util.Set;

import x.org.mulima.api.MulimaService;
import x.org.mulima.api.audio.AudioFormat;
import x.org.mulima.api.file.CachedDir;
import x.org.mulima.api.library.Library;
import x.org.mulima.api.library.LibraryAlbum;

public class DefaultLibrary implements Library {
	private final MulimaService service;
	private final String name;
	private final File rootDir;
	private final AudioFormat format;
	private CachedDir<LibraryAlbum> albums;
	
	public DefaultLibrary(MulimaService service, String name, File rootDir, AudioFormat format) {
		this.service = service;
		this.name = name;
		this.rootDir = rootDir;
		this.format = format;
		//TODO: this.albums = new DefaultCachedDir(rootDir, dirFilter, LibraryAlbumViewer) 
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public File getRootDir() {
		return rootDir;
	}

	@Override
	public AudioFormat getFormat() {
		return format;
	}

	@Override
	public Set<LibraryAlbum> getAll() {
		return albums.getValues();
	}

	@Override
	public LibraryAlbum getSourcedFrom(LibraryAlbum source) {
		return getSourcedFrom(source, true);
	}

	@Override
	public LibraryAlbum getSourcedFrom(LibraryAlbum source, boolean createIfNotFound) {
		if (source == null) {
			throw new NullPointerException("Source must not be null.");
		}
		for (LibraryAlbum album : getAll()) {
			if (source.equals(album.getSource())) {
				return album;
			}
		}
		if (createIfNotFound) {
			return service.getLibraryAlbumFactory().create(this, source);
		} else {
			return null;
		}
	}
}
