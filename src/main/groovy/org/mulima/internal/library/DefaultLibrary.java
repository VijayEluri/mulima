package org.mulima.internal.library;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.internal.file.LeafDirFilter;
import org.mulima.util.FileUtil;
import org.mulima.util.StringUtil;

public class DefaultLibrary implements Library {
	private final LibraryAlbumFactory libAlbumFactory;
	private final String name;
	private final File rootDir;
	private final AudioFormat format;
	private Set<LibraryAlbum> albums = null;
	
	public DefaultLibrary(LibraryAlbumFactory libAlbumFactory, String name, File rootDir, AudioFormat format) {
		this.libAlbumFactory = libAlbumFactory;
		this.name = name;
		this.rootDir = rootDir;
		this.format = format;
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
		if (albums == null) {
			scanAll();
		}
		return albums;
	}
	
	@Override
	public LibraryAlbum getById(UUID id) {
		if (id == null) {
			return null;
		}
		for (LibraryAlbum album : getAll()) {
			if (id.equals(album.getId())) {
				return album;
			}
		}
		return null;
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
			if (source.getId().equals(album.getSourceId())) {
				return album;
			}
		}
		if (createIfNotFound) {
			return createAlbum(source);
		} else {
			return null;
		}
	}
	
	private void scanAll() {
		this.albums = new HashSet<LibraryAlbum>();
		FileFilter filter = new LeafDirFilter();
		for (File dir : FileUtil.listDirsRecursive(getRootDir())) {
			if (filter.accept(dir)) {
				albums.add(libAlbumFactory.create(dir, this));
			}
		}
	}
	
	private LibraryAlbum createAlbum(LibraryAlbum source) {
		String album = null;
		if (source.getAlbum().isSet(GenericTag.ALBUM)) {
			album = source.getAlbum().getFlat(GenericTag.ALBUM);
		} else {
			for (Disc disc : source.getAlbum().getDiscs()) {
				if (disc.isSet(GenericTag.ALBUM)) {
					if (album == null) {
						album = disc.getFlat(GenericTag.ALBUM);
					} else {
						album = StringUtil.commonString(album, disc.getFlat(GenericTag.ALBUM));
					}
				}
			}
		}
		String relPath = StringUtil.makeSafe(source.getAlbum().getFlat(GenericTag.ARTIST)).trim()
			+ File.separator + StringUtil.makeSafe(album).trim();
		File dir = new File(getRootDir(), relPath);
		return libAlbumFactory.create(dir, this);
	}
}
