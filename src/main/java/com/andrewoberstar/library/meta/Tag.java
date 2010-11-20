package com.andrewoberstar.library.meta;

/**
 * Provides consistent access to tag information.
 * @see GenericTag
 */
public interface Tag {
	/**
	 * Gets the <code>GenericTag</code> corresponding
	 * to this <code>Tag</code>.
	 * @return generic form of this tag
	 */
	GenericTag getGeneric();
}
