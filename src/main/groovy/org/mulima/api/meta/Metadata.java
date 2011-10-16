/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mulima.api.meta;

import java.util.List;
import java.util.Map;

import org.mulima.api.meta.GenericTag;


/**
 * Represents an object that holds metadata.  Implementations
 * should support multiple values per tag.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @see Tag
 * @since 0.1.0
 */
public interface Metadata {
	/**
	 * Checks if the given tag is set
	 * on this metadata.
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
	 * Adds all of the metadata from the given object
	 * to this instance.
	 * @param meta a Metadata object with tags (and values)
	 * to be added to this one
	 */
	void addAll(Metadata meta);
	
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
	
	/**
	 * Removes all values for a tag.
	 * @param tag that tag to remove
	 */
	void remove(Tag tag);
	
	/**
	 * Removes all tags and values.
	 */
	void removeAll();
	
	/**
	 * Simplify the metadata, by moving all values
	 * common among all children to their parent.
	 */
	public void tidy();
}
