package org.mulima.internal.meta;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mulima.api.meta.CuePoint;
import org.mulima.api.meta.CueSheet;

/**
 * Default implementation of a cue sheet.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultCueSheet extends AbstractMetadata implements CueSheet {
	private int num;
	private final SortedSet<CuePoint> cuePoints = new TreeSet<CuePoint>();
	
	/**
	 * Constructs an unnumbered cue sheet.
	 */
	public DefaultCueSheet() {
		super();
	}
	
	/**
	 * Constructs a numbered cue sheet.
	 * @param num the disc number
	 */
	public DefaultCueSheet(int num) {
		super();
		this.num = num;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNum() {
		return num;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<CuePoint> getAllCuePoints() {
		return cuePoints;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tidy() {
		//no-op
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(CueSheet o) {
		if (this.equals(o)) {
			return 0;
		} else if (getNum() == o.getNum()) {
			return 1;
		} else { 
			return getNum() < o.getNum() ? -1 : 1;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof DefaultCueSheet) {
			DefaultCueSheet that = (DefaultCueSheet) obj;
			return this.getNum() == that.getNum()
				&& this.getMap().equals(that.getMap())
				&& this.getAllCuePoints().equals(that.getAllCuePoints());
		} else {
			return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = 23;
		result = result * 31 + getNum();
		result = result * 31 + getMap().hashCode();
		result = result * 31 + getAllCuePoints().hashCode();
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[num:");
		builder.append(getNum());
		builder.append(", tags:");
		builder.append(getMap());
		builder.append(", points:");
		builder.append(getAllCuePoints());
		builder.append("]");
		return builder.toString();
	}
}
