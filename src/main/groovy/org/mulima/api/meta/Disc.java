package org.mulima.api.meta;

import java.util.SortedSet;

/**
 * An object representing a disc.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Disc extends Metadata, Comparable<Disc> {
	/**
	 * Gets the disc number.
	 * @return the disc number
	 */
	int getNum();
	
	/**
	 * Gets the tracks that are part of this disc.
	 * @return the tracks
	 */
	SortedSet<Track> getTracks();
	
	/**
	 * Gets the track specified by the parameter.
	 * @param num the number of the track to get
	 * @return the track or {@code null} if it
	 * could not be found
	 */
	Track getTrack(int num);
}
