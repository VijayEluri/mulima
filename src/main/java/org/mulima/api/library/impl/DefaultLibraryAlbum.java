package org.mulima.api.library.impl;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.mulima.api.audio.AudioFile;
import org.mulima.api.library.Library;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;
import org.mulima.cache.Digest;
import org.mulima.job.Context;

public class DefaultLibraryAlbum implements LibraryAlbum {
	private UUID id = null;
	private Album album = null;
	private Digest digest = null;
	private Library lib = null;
	private File dir = null;
	private SortedSet<AudioFile> audioFiles = new TreeSet<AudioFile>();
	private SortedSet<CueSheet> cues = new TreeSet<CueSheet>();
	
	private LibraryAlbum source = null;
	private Digest sourceDigest = null;
	
	
	@Override
	public UUID getId() {
		return id;
	}
	
	@Override
	public void setId(UUID id) {
		this.id = id;
	}

	@Override
	public File getDir() {
		return dir;
	}

	@Override
	public void setDir(File dir) {
		this.dir = dir;
	}
	
	@Override
	public Album getAlbum() {
		return album;
	}
	
	@Override
	public void setAlbum(Album album) {
		this.album = album;
	}

	@Override
	public Library getLib() {
		return lib;
	}
	
	@Override
	public void setLib(Library lib) {
		this.lib = lib;
	}
	
	@Override
	public LibraryAlbum getSource() {
		return source;
	}
	
	@Override
	public void setSource(LibraryAlbum source) {
		this.source = source;
	}
	
	@Override
	public SortedSet<AudioFile> getAudioFiles() {
		return audioFiles;
	}
	
	@Override
	public void setAudioFilest(SortedSet<AudioFile> audioFiles) {
		this.audioFiles = audioFiles;	
	}
	
	@Override
	public AudioFile getAudioFile(int discNum, int trackNum) {
		for (AudioFile file : getAudioFiles()) {
			if (file.getDiscNum() == discNum && file.getTrackNum() == trackNum) {
				return file;
			}
		}
		return null;
	}

	@Override
	public SortedSet<CueSheet> getCues() {
		return cues;
	}
	
	@Override
	public void setCues(SortedSet<CueSheet> cues) {
		this.cues = cues;
	}
	
	@Override
	public CueSheet getCue(int num) {
		for (CueSheet cue : getCues()) {
			if (cue.getNum() == num) {
				return cue;
			}
		}
		return null;
	}
	
	@Override
	public Digest getDigest() {
		return digest;
	}
	
	@Override
	public void setDigest(Digest digest) {
		this.digest = digest;
	}
	
	@Override
	public Digest getSourceDigest() {
		return sourceDigest;
	}

	@Override
	public void setSourceDigest(Digest sourceDigest) {
		this.sourceDigest = sourceDigest;
	}
	
	@Override
	public boolean isUpToDate() throws IOException {
		return isUpToDate(getDigest());
	}

	@Override
	public boolean isUpToDate(boolean checkSource) throws IOException {
		return isUpToDate(getDigest(), checkSource);
	}

	@Override
	public boolean isUpToDate(Digest digest) throws IOException {
		return isUpToDate(digest, true);
	}

	@Override
	public boolean isUpToDate(Digest digest, boolean checkSource) throws IOException {
		if (digest == null) {
			return false;
		} else if (source != null && checkSource && !source.isUpToDate(getSourceDigest())) {
			return false;
		}
		
		Digest current = Context.getCurrent().getDigestService().buildDigest(this);
		return digest == null ? false : digest.equals(current);
	}
}
