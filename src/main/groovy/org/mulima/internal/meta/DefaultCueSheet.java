package org.mulima.internal.meta;

import java.util.SortedSet;
import java.util.TreeSet;

import org.mulima.api.meta.CuePoint;
import org.mulima.api.meta.CueSheet;


public class DefaultCueSheet extends AbstractMetadata implements CueSheet {
	private int num;
	private final SortedSet<CuePoint> cuePoints = new TreeSet<CuePoint>();
	
	public DefaultCueSheet() {
		super();
	}
	
	public DefaultCueSheet(int num) {
		this.num = num;
	}
	
	@Override
	public int getNum() {
		return num;
	}

	@Override
	public SortedSet<CuePoint> getCuePoints() {
		//TODO only include the index 1 points
		return cuePoints;
	}

	@Override
	public SortedSet<CuePoint> getAllCuePoints() {
		return cuePoints;
	}

	@Override
	public void tidy() {
		return;
	}

	@Override
	public int compareTo(CueSheet o) {
		if (getNum() == o.getNum()) {
			return 0;
		} else {
			return getNum() < o.getNum() ? -1 : 1;
		}
	}
}
