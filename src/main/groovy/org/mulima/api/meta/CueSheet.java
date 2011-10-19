package org.mulima.api.meta;

import java.util.SortedSet;

import org.mulima.api.meta.CuePoint;


public interface CueSheet extends Metadata, Comparable<CueSheet> {
	int getNum();
	SortedSet<CuePoint> getCuePoints();
	SortedSet<CuePoint> getAllCuePoints();
}