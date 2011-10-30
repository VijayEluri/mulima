package org.mulima.internal.library;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.CachedDir;
import org.mulima.api.file.CachedFile;
import org.mulima.api.file.Digest;
import org.mulima.api.file.FileService;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.meta.Album;

/**
 * Default implementation of a library album.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultLibraryAlbum implements LibraryAlbum {
	private final File dir;
	private final Library lib;
	private final CachedFile<Album> album;
	private final CachedFile<Digest> digest;
	private final CachedFile<Digest> sourceDigest;
	private final CachedDir<AudioFile> audioFiles;
	
	/**
	 * Constructs a library album from the parameters.
	 * @param fileService the service to use when finding files
	 * @param dir the directory where this album's files reside
	 * @param lib the library this album is contained in
	 */
	public DefaultLibraryAlbum(FileService fileService, File dir, Library lib) {
		this.dir = dir;
		this.lib = lib;
		
		this.album = fileService.createCachedFile(Album.class, new File(dir, "album.xml")); 
		this.digest = fileService.createCachedFile(Digest.class, new File(dir, Digest.FILE_NAME));
		this.sourceDigest = fileService.createCachedFile(Digest.class, new File(dir, Digest.SOURCE_FILE_NAME));
		this.audioFiles = fileService.createCachedDir(AudioFile.class, dir);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public UUID getId() {
		Digest digest = getDigest();
		return digest == null ? null : digest.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getDir() {
		return dir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Library getLib() {
		return lib;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Album getAlbum() {
		return album.getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<AudioFile> getAudioFiles() {
		return audioFiles.getValues();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Digest getDigest() {
		return digest.getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Digest getSourceDigest() {
		return sourceDigest.getValue();
	}
}
