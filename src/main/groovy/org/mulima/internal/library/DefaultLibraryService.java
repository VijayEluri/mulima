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

public class DefaultLibraryService implements LibraryService {
	private final DigestService digestService;
	private final Set<ReferenceLibrary> refLibs;
	private final Set<Library> destLibs;
	
	public DefaultLibraryService(DigestService digestService, Set<ReferenceLibrary> refLibs, Set<Library> destLibs) {
		this.digestService = digestService;
		this.refLibs = Collections.unmodifiableSet(refLibs);
		this.destLibs = Collections.unmodifiableSet(destLibs);
	}
	
	@Override
	public Set<ReferenceLibrary> getRefLibs() {
		return refLibs;
	}

	@Override
	public Set<Library> getDestLibs() {
		return destLibs;
	}
	
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

	@Override
	public boolean isUpToDate(LibraryAlbum libAlbum, boolean checkSource) {
		Digest digest = libAlbum.getDigest();
		
		if (digest == null) {
			return false;
		} else if (checkSource) {
			Digest sourceDigest = libAlbum.getSourceDigest();
			if (sourceDigest != null) {
				LibraryAlbum source = getAlbumById(sourceDigest.getId());
				if (source == null) {
					throw new FatalMulimaException("Source album for " + libAlbum.getId() + " not found: " + sourceDigest.getId());
				} else if (!isUpToDate(source, false)) {
					return false;
				}
			}
		}
		Digest current = digestService.create(libAlbum);
		return digest.equals(current);
	}
}