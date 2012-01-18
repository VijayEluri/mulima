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
package org.mulima.internal.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Metadata;
import org.mulima.api.meta.Tag;


/**
 * An abstract implementation of a metadata object.  Provides
 * map-based tag support.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public abstract class AbstractMetadata implements Metadata {
	private final Map<GenericTag, List<String>> map = new TreeMap<GenericTag, List<String>>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSet(Tag tag) {
		if (tag == null) {
			return false;
		}
		return map.containsKey(tag.getGeneric());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Tag tag, String value) {
		if (tag == null || value == null) {
			return;
		}
		
		GenericTag generic = tag.getGeneric();
		if (!map.containsKey(generic)) {
			map.put(generic, new ArrayList<String>());
		}
		
		map.get(generic).add(value.trim());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAll(Tag tag, List<String> values) {
		if (tag == null || values == null || values.isEmpty()) {
			return;
		}
		
		GenericTag generic = tag.getGeneric();
		if (!map.containsKey(generic)) {
			map.put(generic, new ArrayList<String>());
		}
		
		List<String> tagValues = map.get(generic);
		for (String value : values) {
			tagValues.add(value.trim());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAll(Metadata meta) {
		throw new UnsupportedOperationException("addAll not supported yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getAll(Tag tag) {
		if (tag == null) {
			return Collections.unmodifiableList(new ArrayList<String>());
		}
		GenericTag generic = tag.getGeneric();
		if (map.containsKey(generic)) {
			return Collections.unmodifiableList(map.get(generic));
		} else {
			return Collections.unmodifiableList(new ArrayList<String>());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFirst(Tag tag) {
		if (tag == null) {
			return null;
		}
		GenericTag generic = tag.getGeneric();
		if (map.containsKey(generic) && !map.get(generic).isEmpty()) {
			return map.get(generic).get(0);
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFlat(Tag tag) {
		if (tag == null || !isSet(tag)) {
			return null;
		}
		
		List<String> values = getAll(tag);
		StringBuilder builder = new StringBuilder();
		ListIterator<String> iterator = values.listIterator();
		builder.append(iterator.next());
		while (iterator.hasNext()) {
			if (iterator.nextIndex() == values.size() - 1) {
				builder.append(" & ");
			} else {
				builder.append(", ");	
			}
			builder.append(iterator.next());
		}
		return builder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<GenericTag, List<String>> getMap() {
		return Collections.unmodifiableMap(map);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(Tag tag) {
		if (tag == null) {
			return;
		}
		GenericTag generic = tag.getGeneric();
		map.remove(generic);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAll() {
		map.clear();
	}
	
	/**
	 * Cleans up tags by taking the value of any tag that
	 * is identical in all children and moving it to this object.
	 * @param children list of child metadata objects
	 */
	protected void tidy(Set<? extends Metadata> children) {
		for (Tag tag : GenericTag.values()) {
			if (GenericTag.DISC_NUMBER.equals(tag) || GenericTag.TRACK_NUMBER.equals(tag)) {
				continue;
			}
			
			List<String> values = findCommon(tag, children);
			if (!isSet(tag) && values != null) {
				addAll(tag, values);
				for (Metadata child : children) {
					child.remove(tag);
				}
			}
		}
	}
	
	/**
	 * Finds the common value list for the given tag on the given children.  This will
	 * only find common values if all children have an identical list of values.
	 * @param tag the tag to find the common values of
	 * @param children list of child metadata objects
	 * @return the list of common values or {@code null} if any children have a different value
	 */
	private List<String> findCommon(Tag tag, Set<? extends Metadata> children) {
		List<String> values = null;
		for (Metadata child : children) {
			if (values == null) {
				values = child.getAll(tag);
			} else if (!isMatch(child, tag, values)) {
				return null;
			}
		}
		return values;
	}
	
	/**
	 * Checks if the metadata object has the specified values
	 * set for the specified tag.
	 * @param meta the metadata object
	 * @param tag the tag to match values of
	 * @param values the values to match against
	 * @return {@code true} if it matches, {@code false} otherwise
	 */
	private boolean isMatch(Metadata meta, Tag tag, List<String> values) {
		if (meta.isSet(tag)) {
			return meta.getAll(tag).equals(values);
		} else {
			return false;
		}
	}
}
