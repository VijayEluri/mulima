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

public class DefaultLibraryAlbum implements LibraryAlbum {
	private final File dir;
	private final Library lib;
	private final CachedFile<Album> album;
	private final CachedFile<Digest> digest;
	private final CachedFile<Digest> sourceDigest;
	private CachedDir<AudioFile> audioFiles;
	
	public DefaultLibraryAlbum(FileService fileService, File dir, Library lib) {
		this.dir = dir;
		this.lib = lib;
		
		this.album = fileService.createCachedFile(Album.class, new File(dir, "album.xml")); 
		this.digest = fileService.createCachedFile(Digest.class, new File(dir, Digest.FILE_NAME));
		this.sourceDigest = fileService.createCachedFile(Digest.class, new File(dir, Digest.SOURCE_FILE_NAME));
		this.audioFiles = fileService.createCachedDir(AudioFile.class, dir);
	}
	
	@Override
	public UUID getId() {
		Digest digest = getDigest();
		return digest == null ? null : digest.getId();
	}
	
	@Override
	public UUID getSourceId() {
		Digest digest = getSourceDigest();
		return digest == null ? null : digest.getId();
	}

	@Override
	public File getDir() {
		return dir;
	}

	@Override
	public Library getLib() {
		return lib;
	}

	@Override
	public Album getAlbum() {
		return album.getValue();
	}
	
	@Override
	public Set<AudioFile> getAudioFiles() {
		return audioFiles.getValues();
	}
	
	@Override
	public Digest getDigest() {
		return digest.getValue();
	}
	
	@Override
	public Digest getSourceDigest() {
		return sourceDigest.getValue();
	}
}
