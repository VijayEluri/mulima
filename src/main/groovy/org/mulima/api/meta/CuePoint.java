package org.mulima.api.meta;

/**
 * Represents a cue point on a disc.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface CuePoint extends Comparable<CuePoint> {
	/**
	 * Gets the number of the track this point is for.
	 * @return the track number
	 */
	int getTrack();
	
	/**
	 * Gets the index number of this point.
	 * @return the index number
	 */
	int getIndex();
	
	/**
	 * Gets the timecode for this point.
	 * @return the timecode
	 */
	String getTime();
}
