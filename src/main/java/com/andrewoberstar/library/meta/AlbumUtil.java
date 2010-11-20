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

import com.andrewoberstar.library.util.StringUtil;

public class AlbumUtil {
	public static <T extends Metadata> T closest(Metadata orig, List<T> cands) {
		int min = Integer.MAX_VALUE;
		T closest = null;
		for (T cand : cands) {
			int dist = discDistance(orig, cand);
			if (dist < min) {
				min = dist;
				closest = cand;
			}
		}
		return closest;
	}
	
	public static int discDistance(Metadata orig, Metadata cand) {
		return tagDistance(orig, cand, GenericTag.ARTIST) + tagDistance(orig, cand, GenericTag.ALBUM);
	}
	
	public static int tagDistance(Metadata original, Metadata candidate, Tag tag) {
		String origTag = original.getTags().getFlat(tag);
		String candTag = candidate.getTags().getFlat(tag);
		if (origTag.length() < candTag.length())
			candTag = candTag.substring(0, origTag.length());
		
		return StringUtil.levenshteinDistance(origTag, candTag);
	}
}
