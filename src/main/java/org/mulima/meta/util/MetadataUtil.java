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
package org.mulima.meta.util;

import org.mulima.meta.GenericTag;
import org.mulima.meta.Metadata;
import org.mulima.meta.Tag;
import org.mulima.util.StringUtil;

/**
 * Helper methods for Metadata operations.
 */
public class MetadataUtil {
	/**
	 * This class should never be instantiated.
	 * @throws UnsupportedOperationException always
	 */
	protected MetadataUtil() {
		throw new UnsupportedOperationException("Cannot instantiate this class.");
	}
	
	/**
	 * Calculates the Levenshtein distance between a <code>Tag</code> on
	 * two <code>Metadata</code> objects. 
	 * @param orig the original metadata
	 * @param cand the metadata to compare against
	 * @param tag the tag to compare
	 * @return Levenshtein distance between the two
	 */
	public static int tagDistance(Metadata orig, Metadata cand, Tag tag) {
		String origTag = orig.getFlat(tag);
		String candTag = cand.getFlat(tag);
		if (origTag.length() < candTag.length()) {
			candTag = candTag.substring(0, origTag.length());
		}
		return StringUtil.levenshteinDistance(origTag, candTag);
	}
	
	/**
	 * Calculates the Levenshtein distance between two <code>Metadata</code>
	 * objects.  Uses {@link GenericTag#ARTIST} and {@link GenericTag#ALBUM}
	 * to determine the distance.
	 * @param orig original metadata
	 * @param cand candidate metadata
	 * @return Levenshtein distance
	 */
	public static int discDistance(Metadata orig, Metadata cand) {
		return tagDistance(orig, cand, GenericTag.ARTIST) + tagDistance(orig, cand, GenericTag.ALBUM);
	}
}
