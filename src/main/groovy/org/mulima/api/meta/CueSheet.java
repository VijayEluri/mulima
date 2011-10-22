package org.mulima.api.meta;

import java.util.SortedSet;

/**
 * An object that represents a cue sheet for a disc.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface CueSheet extends Metadata, Comparable<CueSheet> {
	/**
	 * Gets the disc number of this cue sheet.
	 * @return the disc number
	 */
	int getNum();
	
	/**
	 * Gets all cue points that correspond
	 * to a track's start.  (i.e. all points
	 * with index 1) 
	 * @return all index 1 points
	 */
	SortedSet<CuePoint> getCuePoints();
	
	/**
	 * Gets all cue points for this sheet.
	 * @return all cue points
	 */
	SortedSet<CuePoint> getAllCuePoints();
}
