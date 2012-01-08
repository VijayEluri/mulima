package org.mulima.internal.library;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.GenericTag;
import org.mulima.internal.file.LeafDirFilter;
import org.mulima.util.FileUtil;
import org.mulima.util.MetadataUtil;
import org.mulima.util.StringUtil;

/**
 * Default implementation of a library.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultLibrary implements Library {
	private final LibraryAlbumFactory libAlbumFactory;
	private final String name;
	private final File rootDir;
	private final AudioFormat format;
	private Set<LibraryAlbum> albums = null;
	
	/**
	 * Constructs a library from the parameters.
	 * @param libAlbumFactory the factory to create library albums
	 * @param name the name of this library
	 * @param rootDir the root directory of this library
	 * @param format the audio format of the files in this library
	 */
	public DefaultLibrary(LibraryAlbumFactory libAlbumFactory, String name, File rootDir, AudioFormat format) {
		this.libAlbumFactory = libAlbumFactory;
		this.name = name;
		this.rootDir = rootDir;
		this.format = format;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getRootDir() {
		return rootDir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AudioFormat getFormat() {
		return format;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<LibraryAlbum> getAll() {
		if (albums == null) {
			scanAll();
		}
		return albums;
	}
	
	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryAlbum getSourcedFrom(LibraryAlbum source) {
		return getSourcedFrom(source, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryAlbum getSourcedFrom(LibraryAlbum source, boolean createIfNotFound) {
		if (source == null) {
			throw new IllegalArgumentException("Source must not be null.");
		}
		for (LibraryAlbum album : getAll()) {
			if (source.getId().equals(album.getSourceId())) {
				return album;
			}
		}
		return createIfNotFound ? createAlbum(source) : null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public File determineDir(Album meta) {
		String album = null;
		if (meta.isSet(GenericTag.ALBUM)) {
			album = meta.getFlat(GenericTag.ALBUM);
		} else {
			album = MetadataUtil.commonValueFlat(meta.getDiscs(), GenericTag.ALBUM);
		}
		String relPath = StringUtil.makeSafe(meta.getFlat(GenericTag.ARTIST)).trim()
			+ File.separator + StringUtil.makeSafe(album).trim();
		return new File(getRootDir(), relPath);
	}
	
	/**
	 * Scans all directories under the root directory for
	 * library albums.
	 */
	private void scanAll() {
		this.albums = new HashSet<LibraryAlbum>();
		FileFilter filter = new LeafDirFilter();
		for (File dir : FileUtil.listDirsRecursive(getRootDir())) {
			if (filter.accept(dir)) {
				albums.add(libAlbumFactory.create(dir, this));
			}
		}
	}
	
	/**
	 * Creates a new library album from the source.
	 * @param source the source album
	 * @return the new album
	 */
	private LibraryAlbum createAlbum(LibraryAlbum source) {
		File dir = determineDir(source.getAlbum());
		dir.mkdirs();
		return libAlbumFactory.create(dir, this);
	}
}
