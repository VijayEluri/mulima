package org.mulima.api.file.audio;

import org.mulima.api.file.FileHolder;

/**
 * An object representing album artwork.
 * @since 0.1.0
 */
public interface ArtworkFile extends FileHolder {
	/**
	 * Gets the format of the artwork.
	 * @return the format
	 */
	ArtworkFormat getFormat();
}
