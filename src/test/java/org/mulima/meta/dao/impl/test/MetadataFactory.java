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
package org.mulima.meta.dao.impl.test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mulima.meta.Metadata;
import org.mulima.meta.Tag;
import org.mulima.meta.GenericTag;

/**
 * Provides factory methods to create <code>Metadata</code>
 * objects from various forms of <code>Map</code>s.
 * @see Metadata
 */
public class MetadataFactory {
	/**
	 * This class should never be instantiated.
	 * @throws UnsupportedOperationException always
	 */
	protected MetadataFactory() {
		throw new UnsupportedOperationException("Cannot instantiate this class.");
	}
	
	/**
	 * Converts a <code>Map</code> to a <code>TagSupport</code>.
	 * @param map the map to convert
	 * @return a <code>TagSupport</code> containing the specified values
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T extends Metadata> T fromTagList(Map<Tag, List<String>> map, Class<T> type)
		throws InstantiationException, IllegalAccessException {
		T meta = type.newInstance();
		for (Entry<Tag, List<String>> entry : map.entrySet()) {
			Tag tag = entry.getKey();
			meta.add(tag, entry.getValue());
		}
		return meta;
	}
	
	/**
	 * Converts a <code>Map</code> to a <code>TagSupport</code>.
	 * @param map the map to convert
	 * @return a <code>TagSupport</code> containing the specified values
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T extends Metadata> T fromStringList(Map<String, List<String>> map, Class<T> type)
		throws InstantiationException, IllegalAccessException {
		T meta = type.newInstance();
		for (Entry<String, List<String>> entry : map.entrySet()) {
			Tag tag = GenericTag.valueOf(entry.getKey());
			meta.add(tag, entry.getValue());
		}
		return meta;
	}
	
	/**
	 * Converts a <code>Map</code> to a <code>TagSupport</code>.
	 * @param map the map to convert
	 * @return a <code>TagSupport</code> containing the specified values
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T extends Metadata> T fromTagString(Map<Tag, String> map, Class<T> type)
		throws InstantiationException, IllegalAccessException {
		T meta = type.newInstance();
		for (Entry<Tag, String> entry : map.entrySet()) {
			Tag tag = entry.getKey();
			meta.add(tag, entry.getValue());
		}
		return meta;
	}
	
	/**
	 * Converts a <code>Map</code> to a <code>TagSupport</code>.
	 * @param map the map to convert
	 * @return a <code>TagSupport</code> containing the specified values
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T extends Metadata> T fromStringString(Map<String, String> map, Class<T> type)
		throws InstantiationException, IllegalAccessException {
		T meta = type.newInstance();
		for (Entry<String, String> entry : map.entrySet()) {
			Tag tag = GenericTag.valueOf(entry.getKey());
			meta.add(tag, entry.getValue());
		}
		return meta;
	}
}
