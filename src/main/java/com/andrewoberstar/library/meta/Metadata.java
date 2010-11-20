package com.andrewoberstar.library.meta;

/**
 * Provides consistent access to metadata.
 * @see Tag
 */
public interface Metadata {
	/**
	 * Gets the tags associated with this object.
	 * @return tags for this object
	 */
	TagSupport getTags();
}
