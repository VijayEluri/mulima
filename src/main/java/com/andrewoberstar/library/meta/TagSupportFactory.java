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
