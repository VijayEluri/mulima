package org.mulima.api.library;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.Digest;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.meta.Album;

/**
 * An object representing an album as stored
 * in a library.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface LibraryAlbum {
	/**
	 * Gets the ID of this album.
	 * @return the ID
	 */
	UUID getId();
<<<<<<< HEAD
	UUID getSourceId();
=======
	
	/**
	 * Gets the directory this album is stored in.
	 * @return the directory
	 */
>>>>>>> 2e6054cd481debdee6a804df9088c8eef7e3b7cf
	File getDir();
	
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
}
