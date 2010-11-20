package com.andrewoberstar.library.meta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of <code>TagSupport</code> backed by
 * a <code>LinkedHashMap</code>.
 */
public class TagSupportImpl implements TagSupport {
	private final Map<GenericTag, List<String>> map = new LinkedHashMap<GenericTag, List<String>>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSet(Tag tag) {
		return map.containsKey(tag.getGeneric());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<GenericTag, List<String>> getMap() {
		return map;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Tag tag, String value) {
		if (value == null)
			return;
		
		GenericTag generic = tag.getGeneric();
		if (!map.containsKey(generic))
			map.put(generic, new ArrayList<String>());
		
		map.get(generic).add(value.trim());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Tag tag, List<String> values) {
		if (values == null)
			return;
		
		GenericTag generic = tag.getGeneric();
		if (!map.containsKey(generic))
			map.put(generic, new ArrayList<String>());
		
		List<String> tagValues = map.get(generic);
		for (String value : values) {
			tagValues.add(value.trim());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getAll(Tag tag) {
		GenericTag generic = tag.getGeneric();
		return map.get(generic);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFirst(Tag tag) {
		GenericTag generic = tag.getGeneric();
		if (map.containsKey(generic) && map.get(generic).size() > 0)
			return map.get(generic).get(0);
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFlat(Tag tag) {
		List<String> values = getAll(tag);
		if (values == null)
			return null;
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < values.size(); i++) {
			if (i != 0) {
				if (values.size() != 2)
					builder.append(",");
				if (values.size() - 1 == i)
					builder.append(" &");
				builder.append(" ");
			}
			builder.append(values.get(i));
		}
		
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof TagSupport) {
			TagSupport that = (TagSupport) obj;
			return this.getMap().equals(that.getMap());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return map.hashCode();
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
}