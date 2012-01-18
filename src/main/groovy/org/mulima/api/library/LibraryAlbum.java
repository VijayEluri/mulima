package org.mulima.api.library;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.Digest;
import org.mulima.api.file.audio.ArtworkFile;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.meta.Album;
import org.mulima.api.meta.CueSheet;

/**
 * An object representing an album as stored
 * in a library.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface LibraryAlbum extends Comparable<LibraryAlbum> {
	/**
	 * Gets the ID of this album.
	 * @return the ID
	 */
	UUID getId();

	/**
	 * Gets the ID of this album's source.
	 * @return the source ID
	 */
	UUID getSourceId();
	
	/**
	 * Gets a human readable name for this album.
	 * @return the name
	 */
	String getName();
	
	/**
	 * Gets the directory this album is stored in.
	 * @return the directory
	 */
	File getDir();
	
	/**
	 * Sets the directory this album is stored in.
	 * If the dir was already set, this will rename
	 * the existing directory.
	 * @param dir the new directory
	 */
	void setDir(File dir);
	
	/**
	 * Gets the library this album is stored in.
	 * @return the library
	 */
	Library getLib();
	
	/**
	 * Gets the album metadata that goes with this album.
	 * @return the metadata
	 */
	Album getAlbum();
	
	/**
	 * Gets the audio files for this album.
	 * @return the audio files
	 */
	Set<AudioFile> getAudioFiles();
	
	/**
	 * Gets the cue sheets for this album.
	 * @return the cue sheets
	 */
	Set<CueSheet> getCueSheets();
	
	/**
	 * Gets the artwork files for this album.
	 * @return the artwork
	 */
	Set<ArtworkFile> getArtwork();
	
	/**
	 * Gets a digest representing the state
	 * of this album the last time it was updated.
	 * @return the digest
	 */
	Digest getDigest();
	
	/**
	 * Gets a digest representing the state
	 * of the source album the last time this
	 * album was updated.
	 * @return the source digest
	 */
	Digest getSourceDigest();
	
	/**
	 * Cleans up all files (except digest files), generally
	 * in preparation for a new conversion.
	 */
	void cleanDir();
}
