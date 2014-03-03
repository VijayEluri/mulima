package org.mulima.internal.library;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mulima.api.file.Digest;
import org.mulima.api.file.DigestService;
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.LibraryAlbumFactory;
import org.mulima.api.library.LibraryService;
import org.mulima.api.library.ReferenceLibrary;
import org.mulima.api.service.MulimaProperties;
import org.mulima.exception.UncheckedMulimaException;
import org.mulima.internal.service.MulimaPropertiesSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a library service.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class DefaultLibraryService extends MulimaPropertiesSupport implements LibraryService {
	private static final Logger logger = LoggerFactory.getLogger(DefaultLibraryService.class);
	private LibraryAlbumFactory libAlbumFactory;
	private final DigestService digestService;
	private Set<ReferenceLibrary> refLibs;
	private Set<Library> destLibs;

	/**
	 * Constructs a library service from the parameters.
	 * @param libAlbumFactory the library album factory to pass to libraries
	 * @param digestService the service to use when calculating digests
	 * @param refLibs the reference libraries
	 * @param destLibs the destination libraries
	 */
	@Autowired
	public DefaultLibraryService(LibraryAlbumFactory libAlbumFactory, DigestService digestService) {
		this.libAlbumFactory = libAlbumFactory;
		this.digestService = digestService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<ReferenceLibrary> getRefLibs() {
		if (refLibs == null) {
			this.refLibs = createLibs(ReferenceLibrary.class, getProperties().withScope("reflib"));
		}
		return refLibs;
	}

	public void setRefLibs(Set<ReferenceLibrary> refLibs) {
		if (this.refLibs != null) {
			throw new IllegalStateException("Cannot change refernce libraries after they have been set.");
		}
		this.refLibs = Collections.unmodifiableSet(refLibs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Library> getDestLibs() {
		if (destLibs == null) {
			this.destLibs = createLibs(Library.class, getProperties().withScope("lib"));
		}
		return destLibs;
	}

	public void setDestLibs(Set<Library> destLibs) {
		if (this.destLibs != null) {
			throw new IllegalStateException("Cannot change destination libraries after they have been set.");
		}
		this.destLibs = Collections.unmodifiableSet(destLibs);
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
		return isUpToDate(libAlbum, digest);
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
			throw new UncheckedMulimaException("Source album for " + libAlbum.getId() + " not found: " + sourceDigest.getId());
		} else {
			return isUpToDate(source, sourceDigest);
		}
	}

	/**
	 * Checks if an album is up to date compared to the cached
	 * digest.
	 * @param album the album to check
	 * @param digest the digest representing a previous state
	 * @return {@code true} if up to date, {@code false} otherwise
	 */
	private boolean isUpToDate(LibraryAlbum album, Digest cached) {
		logger.trace("Beginning isUpToDate for {} ({})", album, album.getLib());
		if (cached == null) {
			return false;
		}
		Digest current = digestService.create(album);
		return cached.equals(current);
	}

	@SuppressWarnings("unchecked")
	private <T extends Library> Set<T> createLibs(Class<T> type, MulimaProperties props) {
		Set<T> libs = new HashSet<T>();
		Set<String> names = props.getSubScopes();
		for (String name : names) {
			MulimaProperties namedProps = props.withScope(name);
			File dir = new File(namedProps.getProperty("dir"));
			AudioFormat format = AudioFormat.valueOf(namedProps.getProperty("format"));
			if (type.isAssignableFrom(ReferenceLibrary.class)) {
				libs.add((T) new DefaultReferenceLibrary(libAlbumFactory, name, dir, format));
			} else {
				libs.add((T) new DefaultLibrary(libAlbumFactory, name, dir, format));
			}
		}
		return libs;
	}

	@Override
	protected List<String> getScope() {
		return Collections.emptyList();
	}
}
