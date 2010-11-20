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
