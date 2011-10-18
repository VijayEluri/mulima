package org.mulima.internal.meta;

import java.util.Collections;
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
		SortedSet<CuePoint> points = new TreeSet<CuePoint>();
		for (CuePoint point : getAllCuePoints()) {
			if (point.getIndex() == 1) {
				points.add(point);
			}
		}
		return Collections.unmodifiableSortedSet(points);
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
