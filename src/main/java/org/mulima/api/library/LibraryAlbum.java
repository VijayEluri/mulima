/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mulima.api.library;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.mulima.api.audio.AudioFile;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;
import org.mulima.cache.Digest;
import org.mulima.cache.DigestBuilder;

/**
 * A library album is a collection of all items that correspond
 * with an album within a particular library.  This includes 
 * album metadata, the directory it is contained in, a list of 
 * audio files within the directory, a list of cue sheets within
 * the directory, and a reference to its parent library.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class LibraryAlbum {
	private UUID id = null;
	private Album album = null;
	private Library lib = null;
	private File dir = null;
	private SortedSet<AudioFile> audioFiles = new TreeSet<AudioFile>();
	private SortedSet<CueSheet> cues = new TreeSet<CueSheet>();
	
	private Digest digest;
	private Digest sourceDigest;
	
	/**
	 * Gets the ID of this object.
	 * @return the ID
	 */
	public UUID getId() {
		return id;
	}
	
	/**
	 * Sets the ID of this object.
	 * @param id the ID
	 */
	public void setId(UUID id) {
		this.id = id;
	}
	
	/**
	 * Gets the album metadata for this library album.
	 * @return the album
	 */
	public Album getAlbum() {
		return album;
	}
	
	/**
	 * Sets the album metadata for this library album.
	 * @param album the album to set
	 */
	public void setAlbum(Album album) {
		this.album = album;
	}

	/**
	 * Gets the parent library of this library album.
	 * @return the library
	 */
	public Library getLib() {
		return lib;
	}
	
	/**
	 * Sets the parent library of this library album.
	 * @param lib the library
	 */
	public void setLib(Library lib) {
		this.lib = lib;
	}

	/**
	 * Gets the directory this library album is in.
	 * @return the directory
	 */
	public File getDir() {
		return dir;
	}
	
	/**
	 * Sets the directory this library album is in.
	 * @param dir the directory
	 */
	public void setDir(File dir) {
		this.dir = dir;
	}
	
	/**
	 * Gets a set of all audio files in this library
	 * album.
	 * @return the audio files
	 */
	public SortedSet<AudioFile> getAudioFiles() {
		return audioFiles;
	}
	
	/**
	 * Sets a set of all audio files in this library
	 * album. 
	 * @param audioFiles the audio files
	 */
	public void setAudioFiles(SortedSet<AudioFile> audioFiles) {
		this.audioFiles = audioFiles;
	}

	/**
	 * Gets a list of all cue sheets in this library
	 * album.
	 * @return the cue sheets
	 */
	public SortedSet<CueSheet> getCues() {
		return cues;
	}

	/**
	 * Sets a list of all cue sheets in this library
	 * album.
	 * @param cues the cue sheets
	 */
	public void setCues(SortedSet<CueSheet> cues) {
		this.cues = cues;
	}
	
	public CueSheet getCue(int num) {
		for (CueSheet cue : cues) {
			if (cue.getNum() == num) {
				return cue;
			}
		}
		return null;
	}
	
	public Digest getDigest() {
		return digest;
	}
	
	public void setDigest(Digest digest) {
		this.digest = digest;
	}
	
	public Digest getSourceDigest() {
		return sourceDigest;
	}
	
	public void setSourceDigest(Digest sourceDigest) {
		this.sourceDigest = sourceDigest;
	}
	
	/**
	 * Determines whether this album needs to be updated.
	 * @return <code>true</code> if it is up to date, <code>false</code> otherwise
	 * @throws IOException if there is a problem generating the digests
	 */
	public boolean isUpToDate() throws IOException {
		return isUpToDate(getDigest());
	}
	
	/**
	 * Determines whether this album needs to be updated.
	 * @param checkSource whether to check if the source digest is up to date
	 * @return <code>true</code> if it is up to date, <code>false</code> otherwise
	 * @throws IOException if there is a problem generating the digests
	 */
	public boolean isUpToDate(boolean checkSource) throws IOException {
		return isUpToDate(getDigest(), checkSource);
	}
	
	/**
	 * Determines whether this album needs to be updated.
	 * @param digest the digest to check against the current state
	 * @return <code>true</code> if it is up to date, <code>false</code> otherwise
	 * @throws IOException if there is a problem generating the digests
	 */
	public boolean isUpToDate(Digest digest) throws IOException {
		return isUpToDate(digest, true);
	}
	
	/**
	 * Determines whether this album needs to be updated.
	 * @param digest the digest to check against the current state
	 * @param checkSource whether to check if the source digest is up to date
	 * @return <code>true</code> if it is up to date, <code>false</code> otherwise
	 * @throws IOException if there is a problem generating the digests
	 */
	public boolean isUpToDate(Digest digest, boolean checkSource) throws IOException {
		if (checkSource && sourceDigest != null) {
			LibraryAlbum source = getLib().get(sourceDigest.getId());
			if (!source.isUpToDate(sourceDigest)) {
				return false;
			}
		}
		Digest current = new DigestBuilder(this).build();
		return digest == null ? false : digest.equals(current);
	}
}
