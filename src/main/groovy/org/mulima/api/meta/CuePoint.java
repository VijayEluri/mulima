package org.mulima.api.meta;

public interface CuePoint extends Comparable<CuePoint> {
	int getTrack();
	int getIndex();
	String getTime();
}
