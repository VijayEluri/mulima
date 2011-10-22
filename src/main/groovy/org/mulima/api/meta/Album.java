package org.mulima.api.meta;

import java.util.SortedSet;

/**
 * An object representing the metadata of an album.  This includes
 * reference to the discs that make up the album.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Album extends Metadata {
	/**
	 * The standard name for an album file.
	 */
	String FILE_NAME = "album.xml";
	
	/**
	 * Gets the discs that are part of this album.
	 * @return the discs
	 */
	SortedSet<Disc> getDiscs();
	
	/**
	 * Gets the disc specified by the parameter.
	 * @param num the number of the disc to get
	 * @return the disc or {@code null} if not found
	 */
	Disc getDisc(int num);
	
	/**
	 * Flattens all metadata on this album down to
	 * a list of the component tracks.
	 * @return a list of tracks with all metadata
	 */
	SortedSet<Track> flatten();
}
