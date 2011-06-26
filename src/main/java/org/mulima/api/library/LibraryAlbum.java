package org.mulima.api.library;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.UUID;

import org.mulima.api.audio.AudioFile;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;
import org.mulima.cache.Digest;

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
public interface LibraryAlbum {
	/**
	 * Gets the ID of this object.
	 * @return the ID
	 */
	UUID getId();
	
	/**
	 * Sets the ID of this object.
	 * @param id the ID
	 */
	void setId(UUID id);
	
	/**
	 * Gets the directory this library album is in.
	 * @return the directory
	 */
	File getDir();
	
	/**
	 * Sets the directory this library album is in.
	 * @param dir the directory
	 */
	void setDir(File dir);
	
	/**
	 * Gets the album metadata for this library album.
	 * @return the album
	 */
	Album getAlbum();
	
	/**
	 * Sets the album metadata for this library album.
	 * @param album the album
	 */
	void setAlbum(Album album);
	
	/**
	 * Gets the parent library of this library album.
	 * @return the library
	 */
	Library getLib();
	
	/**
	 * Sets the parent library of this library album.
	 * @param lib the library
	 */
	void setLib(Library lib);
	
	/**
	 * Gets the source album this album was
	 * converted from.
	 * @return the source or <code>null</code> if this
	 * is the reference
	 */
	LibraryAlbum getSource();
	
	/**
	 * Sets the source album this album was
	 * converted from.
	 * @param source the source
	 */
	void setSource(LibraryAlbum source);
	
	/**
	 * Gets a set of all audio files in this library
	 * album.
	 * @return the audio files
	 */
	SortedSet<AudioFile> getAudioFiles();
	
	/**
	 * Sets a set of all audio files in this library
	 * album.
	 * @param audioFiles the audio files
	 */
	void setAudioFilest(SortedSet<AudioFile> audioFiles);
	
	/**
	 * Gets the audio file specified by the parms.
	 * @param discNum the disc number of the track
	 * @param trackNum the track number of the track
	 * @return the audio file
	 */
	AudioFile getAudioFile(int discNum, int trackNum);
	
	/**
	 * Gets a list of all cue sheets in this library
	 * album.
	 * @return the cue sheets
	 */
	SortedSet<CueSheet> getCues();
	
	/**
	 * Sets a list of all cue sheets in this library
	 * album.
	 * @param cues the cue sheets
	 */
	void setCues(SortedSet<CueSheet> cues);
	
	/**
	 * Gets the cue sheet specified by the parms. 
	 * @param num the disc number of the cue
	 * @return the cue
	 */
	CueSheet getCue(int num);
	
	/**
	 * Gets the digest from the last time this album was updated.
	 * @return the digest
	 */
	Digest getDigest();
	
	/**
	 * Sets the digest from the last time this album was updated.
	 * @param digest the digest
	 */
	void setDigest(Digest digest);
	
	/**
	 * Gets the digest of the source album from the last time this
	 * album was updated.
	 * @return the source digest
	 */
	Digest getSourceDigest();
	
	/**
	 * Sets the digest of the source album from the last time this
	 * album was updated.
	 * @param sourceDigest the source digest
	 */
	void setSourceDigest(Digest sourceDigest);
	
	/**
	 * Determines whether this album needs to be updated.
	 * @return <code>true</code> if it is up to date, <code>false</code> otherwise
	 * @throws IOException if there is a problem generating the digests
	 */
	boolean isUpToDate() throws IOException;
	
	/**
	 * Determines whether this album needs to be updated.
	 * @param checkSource whether to check if the source digest is up to date
	 * @return <code>true</code> if it is up to date, <code>false</code> otherwise
	 * @throws IOException if there is a problem generating the digests
	 */
	boolean isUpToDate(boolean checkSource) throws IOException;
	
	/**
	 * Determines whether this album needs to be updated.
	 * @param digest the digest to check against the current state
	 * @return <code>true</code> if it is up to date, <code>false</code> otherwise
	 * @throws IOException if there is a problem generating the digests
	 */
	boolean isUpToDate(Digest digest) throws IOException;
	
	/**
	 * Determines whether this album needs to be updated.
	 * @param digest the digest to check against the current state
	 * @param checkSource whether to check if the source digest is up to date
	 * @return <code>true</code> if it is up to date, <code>false</code> otherwise
	 * @throws IOException if there is a problem generating the digests
	 */
	boolean isUpToDate(Digest digest, boolean checkSource) throws IOException;
}
