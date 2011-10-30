package org.mulima.internal.library;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.Digest;
import org.mulima.api.file.DigestService;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryService;
import org.mulima.api.library.ReferenceLibrary;
import org.mulima.exception.FatalMulimaException;

/**
 * Default implementation of a library service.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultLibraryService implements LibraryService {
	private final DigestService digestService;
	private final Set<ReferenceLibrary> refLibs;
	private final Set<Library> destLibs;
	
	/**
	 * Constructs a library service from the parameters.
	 * @param digestService the service to use when calculating digests
	 * @param refLibs the reference libraries
	 * @param destLibs the destination libraries
	 */
	public DefaultLibraryService(DigestService digestService, Set<ReferenceLibrary> refLibs, Set<Library> destLibs) {
		this.digestService = digestService;
		this.refLibs = Collections.unmodifiableSet(refLibs);
		this.destLibs = Collections.unmodifiableSet(destLibs);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<ReferenceLibrary> getRefLibs() {
		return refLibs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Library> getDestLibs() {
		return destLibs;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Library getLibFor(File dir) {
		Set<Library> allLibs = new HashSet<Library>();
		allLibs.addAll(getRefLibs());
		allLibs.addAll(getDestLibs());
		
		for (Library lib : allLibs) {
			File rootDir = lib.getRootDir();
			File temp = dir;
			while (temp.getParentFile() != null) {
				temp = temp.getParentFile();
				if (temp.equals(rootDir)) {
					return lib;
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryAlbum getAlbumById(UUID id) {
		Set<Library> allLibs = new HashSet<Library>();
		allLibs.addAll(getRefLibs());
		allLibs.addAll(getDestLibs());
		
		for (Library lib : allLibs) {
			LibraryAlbum album = lib.getById(id);
			if (album != null) {
				return album;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUpToDate(LibraryAlbum libAlbum, boolean checkSource) {
		Digest digest = libAlbum.getDigest();
		
		if (digest == null) {
			return false;
		} else if (checkSource && !isSourceUpToDate(libAlbum)) {
			return false;
		}
		Digest current = digestService.create(libAlbum);
		return digest.equals(current);
	}
	
	/**
	 * Checks if the source of the specified album
	 * is up to date.
	 * @param libAlbum the album to check
	 * @return {@code true} if the source is up to
	 * date, {@code false} otherwise
	 */
	private boolean isSourceUpToDate(LibraryAlbum libAlbum) {
		Digest sourceDigest = libAlbum.getSourceDigest();
		if (sourceDigest == null) {
			return true;
		}
		LibraryAlbum source = getAlbumById(sourceDigest.getId());
		if (source == null) {
			throw new FatalMulimaException("Source album for " + libAlbum.getId() + " not found: " + sourceDigest.getId());
		} else if (!isUpToDate(source, false)) {
			return false;
		} else {
			return true;
		}
	}
}
