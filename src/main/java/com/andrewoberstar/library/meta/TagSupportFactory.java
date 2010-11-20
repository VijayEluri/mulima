/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
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

package com.andrewoberstar.library.meta;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Provides factory methods to create <code>TagSupport</code>
 * objects from various forms of <code>Map</code>s.
 *@see TagSupport
 */
public class TagSupportFactory {
	/**
	 * Converts a <code>Map</code> to a <code>TagSupport</code>.
	 * @param map the map to convert
	 * @return a <code>TagSupport</code> containing the specified values
	 */
	public static TagSupport fromTagList(Map<Tag, List<String>> map) {
		TagSupport tags = new TagSupportImpl();
		for (Entry<Tag, List<String>> entry : map.entrySet()) {
			Tag tag = entry.getKey();
			tags.add(tag, entry.getValue());
		}
		return tags;
	}
	
	/**
	 * Converts a <code>Map</code> to a <code>TagSupport</code>.
	 * @param map the map to convert
	 * @return a <code>TagSupport</code> containing the specified values
	 */
	public static TagSupport fromStringList(Map<String, List<String>> map) {
		TagSupport tags = new TagSupportImpl();
		for (Entry<String, List<String>> entry : map.entrySet()) {
			Tag tag = GenericTag.valueOf(entry.getKey());
			tags.add(tag, entry.getValue());
		}
		return tags;
	}
	
	/**
	 * Converts a <code>Map</code> to a <code>TagSupport</code>.
	 * @param map the map to convert
	 * @return a <code>TagSupport</code> containing the specified values
	 */
	public static TagSupport fromTagString(Map<Tag, String> map) {
		TagSupport tags = new TagSupportImpl();
		for (Entry<Tag, String> entry : map.entrySet()) {
			Tag tag = entry.getKey();
			tags.add(tag, entry.getValue());
		}
		return tags;
	}
	
	/**
	 * Converts a <code>Map</code> to a <code>TagSupport</code>.
	 * @param map the map to convert
	 * @return a <code>TagSupport</code> containing the specified values
	 */
	public static TagSupport fromStringString(Map<String, String> map) {
		TagSupport tags = new TagSupportImpl();
		for (Entry<String, String> entry : map.entrySet()) {
			Tag tag = GenericTag.valueOf(entry.getKey());
			tags.add(tag, entry.getValue());
		}
		return tags;
	}
}
