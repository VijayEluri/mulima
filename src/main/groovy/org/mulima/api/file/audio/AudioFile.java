package org.mulima.api.file.audio;

import org.mulima.api.audio.AudioFormat;
import org.mulima.api.file.FileHolder;
import org.mulima.api.meta.Metadata;

/**
 * An object representing an audio file.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface AudioFile extends FileHolder {
	/**
	 * Gets the format of this audio file.
	 * @return the format
	 */
	AudioFormat getFormat();
	
	/**
	 * Gets the metadata associated with this file.
	 * @return the metadata
	 */
	Metadata getMeta();
	
	/**
	 * Sets the metadata associated with this file.
	 * @param meta the metadata
	 */
	void setMeta(Metadata meta);
}
