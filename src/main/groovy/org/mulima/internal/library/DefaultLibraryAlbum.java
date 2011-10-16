package org.mulima.internal.library;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.audio.file.AudioFile;
import org.mulima.api.file.CachedDir;
import org.mulima.api.file.CachedFile;
import org.mulima.api.file.Digest;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;
import org.mulima.api.service.MulimaService;
import org.mulima.internal.file.DefaultCachedDir;


public class DefaultLibraryAlbum implements LibraryAlbum {
	private final MulimaService service;
	private final UUID id;
	private final File dir;
	private final Library lib;
	private final LibraryAlbum source;
	private final CachedFile<Album> album;
	private final CachedDir<CueSheet> cues;
	private final CachedFile<Digest> digest;
	private final CachedFile<Digest> sourceDigest;
	private CachedDir<AudioFile> audioFiles;
	
	public DefaultLibraryAlbum(MulimaService service, UUID id, File dir, Library lib, LibraryAlbum source) {
		this.service = service;
		this.dir = dir;
		this.lib = lib;
		
		this.album = service.getCachedFileFactory().valueOf(new File(dir, "album.xml"), Album.class);
		this.cues = new DefaultCachedDir<CueSheet>(service.getParser(CueSheet.class), dir, new CueSheetFilter());
		
		this.digest = service.getCachedFileFactory().valueOf(new File(dir, Digest.FILE_NAME), Digest.class);
		this.sourceDigest = service.getCachedFileFactory().valueOf(new File(dir, Digest.SOURCE_FILE_NAME), Digest.class);
		
		if (id == null) {
			this.id = getDigest().getId();
		} else {
			this.id = id;
		}
		
		if (source == null) {
			this.source = service.getAlbumById(getSourceDigest().getId());
		} else {
			this.source = source;
		}
		
		this.audioFiles = new DefaultCachedDir<AudioFile>(service.getParser(AudioFile.class), dir);
	}
	
	@Override
	public UUID getId() {
		return id;
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
	public LibraryAlbum getSource() {
		return source;
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

	@Override
	public boolean isUpToDate() {
		return isUpToDate(getDigest());
	}

	@Override
	public boolean isUpToDate(boolean checkSource) {
		return isUpToDate(getDigest(), checkSource);
	}

	@Override
	public boolean isUpToDate(Digest digest) {
		return isUpToDate(digest, true);
	}

	@Override
	public boolean isUpToDate(Digest digest, boolean checkSource) {
		if (digest == null) {
			return false;
		} else if (source != null && checkSource && !source.isUpToDate(getSourceDigest())) {
			return false;
		}
		Digest current = service.getDigestService().build(this);
		return digest == null ? false : digest.equals(current);
	}
	
	private static class CueSheetFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".cue");
		}
	}
}
