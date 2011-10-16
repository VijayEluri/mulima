package org.mulima.internal.library;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.file.CachedDir;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.service.MulimaService;
import org.mulima.internal.file.DefaultCachedDir;
import org.mulima.internal.file.LeafDirFilter;


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
		this.albums = new DefaultCachedDir<LibraryAlbum>(service.getParser(LibraryAlbum.class), rootDir, new LeafDirFilter());
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
	public LibraryAlbum getById(UUID id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Implement this");
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
