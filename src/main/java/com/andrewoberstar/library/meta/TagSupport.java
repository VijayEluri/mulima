package com.andrewoberstar.library.meta;

import java.util.List;
import java.util.Map;

/**
 * Provides a consistent way to access <code>Tag</code> values.
 * @see Tag, GenericTag
 */
public interface TagSupport {
	/**
	 * Checks if the given tag is set
	 * on this object.
	 * @param tag the tag to check.
	 * @return <code>true</code> if it is set, <code>false</code> otherwise
	 */
	boolean isSet(Tag tag);
	
	/**
	 * Adds the specified value for the specified tag.
	 * @param tag the tag to add <code>value</code> for
	 * @param value the value to add to <code>tag</code>
	 */
	void add(Tag tag, String value);
	
	/**
	 * Adds all of the specified values for the specified tag.
	 * @param tag the tag to add <code>values</code> for
	 * @param values the values to add to <code>tag</code>
	 */
	void add(Tag tag, List<String> values);
	
	/**
	 * Gets all values for the given tag.
	 * @param tag the tag to get values for
	 * @return the values of <code>tag</code>
	 */
	List<String> getAll(Tag tag);
	
	/**
	 * Gets the first value for a given tag.
	 * @param tag the tag to get the value of
	 * @return the first value for <code>tag</code>
	 */
	String getFirst(Tag tag);
	
	/**
	 * Gets all values for a tag in a single <code>String</code>.
	 * @param tag the tag to get the values of
	 * @return all values of <code>tag</code> in <code>String</code> format
	 */
	String getFlat(Tag tag);
	
	/**
	 * Gets a <code>Map</code> of the values backing this
	 * object.
	 * @return map of values
	 */
	Map<GenericTag, List<String>> getMap();
}
