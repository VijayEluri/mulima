package org.mulima.internal.library;

import java.io.File;

import org.mulima.api.file.FileService;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;

/**
 * Default implementation of a library album factory.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultLibraryAlbumFactory implements LibraryAlbumFactory {
	private final FileService fileService;
	
	/**
	 * Constructs a factory that will use the specified file service.
	 * @param fileService the file service to use
	 */
	public DefaultLibraryAlbumFactory(FileService fileService) {
		this.fileService = fileService;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryAlbum create(File dir, Library lib) {
		return new DefaultLibraryAlbum(fileService, dir, lib);
	}
}
