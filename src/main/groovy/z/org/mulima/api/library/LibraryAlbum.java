package z.org.mulima.api.library;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.UUID;

import z.org.mulima.api.file.AudioFile;
import z.org.mulima.api.meta.Album;
import z.org.mulima.cache.Digest;

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
	 * Gets the directory this library album is in.
	 * @return the directory
	 */
	File getDir();
	
	/**
	 * Gets the album metadata for this library album.
	 * @return the album
	 */
	Album getAlbum();
	
	/**
	 * Gets the source album this album was
	 * converted from.
	 * @return the source or <code>null</code> if this
	 * is the reference
	 */
	LibraryAlbum getSource();
	
	/**
	 * Gets a set of all audio files in this library
	 * album.
	 * @return the audio files
	 */
	SortedSet<AudioFile> getAudioFiles();
	
	/**
	 * Gets the digest from the last time this album was updated.
	 * @return the digest
	 */
	Digest getDigest();
	
	/**
	 * Gets the digest of the source album from the last time this
	 * album was updated.
	 * @return the source digest
	 */
	Digest getSourceDigest();
	
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
