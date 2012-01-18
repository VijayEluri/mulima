package org.mulima.api.file.audio;

import org.mulima.api.meta.Track;

/**
 * An object representing an audio file
 * of a track's worth of music.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface TrackFile extends AudioFile {
	/**
	 * {@inheritDoc}
	 */
	Track getMeta();
	
	/**
	 * Gets the disc number of this file.
	 * @return the disc number
	 */
	int getDiscNum();
	
	/**
	 * Gets the track number of this file.
	 * @return the track number
	 */
	int getTrackNum();
}
