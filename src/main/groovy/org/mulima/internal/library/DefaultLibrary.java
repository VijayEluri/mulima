package org.mulima.internal.library;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.file.FileService;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.internal.file.LeafDirFilter;
import org.mulima.util.FileUtil;
import org.mulima.util.StringUtil;

/**
 * Default implementation of a library.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultLibrary implements Library {
	private final FileService fileService;
	private final String name;
	private final File rootDir;
	private final AudioFormat format;
	private Set<LibraryAlbum> albums = null;
	
	/**
	 * Constructs a library from the parameters.
	 * @param fileService the service to pass to child albums
	 * @param name the name of this library
	 * @param rootDir the root directory of this library
	 * @param format the audio format of the files in this library
	 */
	public DefaultLibrary(FileService fileService, String name, File rootDir, AudioFormat format) {
		this.fileService = fileService;
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
			throw new NullPointerException("Source must not be null.");
		}
		for (LibraryAlbum album : getAll()) {
			if (source.getId().equals(album.getSourceDigest().getId())) {
				return album;
			}
		}
		if (createIfNotFound) {
			return createAlbum(source);
		} else {
			return null;
		}
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
				albums.add(new DefaultLibraryAlbum(fileService, dir, this));
			}
		}
	}
	
	/**
	 * Creates a new library album from the source.
	 * @param source the source album
	 * @return the new album
	 */
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
		return new DefaultLibraryAlbum(fileService, dir, this);
	}
}
