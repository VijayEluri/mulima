package org.mulima.internal.meta;

import org.mulima.api.meta.CuePoint;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Track;

public class DefaultTrack extends AbstractMetadata implements Track {
	private final CuePoint startPoint;
	private final CuePoint endPoint;
	
	public DefaultTrack() {
		this(null, null);
	}
	
	public DefaultTrack(CuePoint startPoint, CuePoint endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}
	
	@Override
	public int getNum() {
		return Integer.valueOf(getFirst(GenericTag.TRACK_NUMBER));
	}

	@Override
	public int getDiscNum() {
		try {
			return Integer.valueOf(getFirst(GenericTag.DISC_NUMBER));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	@Override
	public CuePoint getStartPoint() {
		return startPoint;
	}

	@Override
	public CuePoint getEndPoint() {
		return endPoint;
	}
	
	@Override
	public void tidy() {
		return;
	}
	
	@Override
	public int compareTo(Track o) {
		if (o == null) {
			throw new NullPointerException("Cannot compare to null value");
		}
		if (this.equals(o)) {
			return 0;
		} else if (getDiscNum() == o.getDiscNum()) {
			if (getNum() == o.getNum()) {
				return 1;
			} else {
				return getNum() < o.getNum() ? -1 : 1;
			}
		} else {
			return getDiscNum() < o.getDiscNum() ? -1 : 1;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof DefaultTrack) {
			DefaultTrack that = (DefaultTrack) obj;
			return this.getMap().equals(that.getMap())
				&& ((this.getStartPoint() == null && that.getStartPoint() == null) || (this.getStartPoint().equals(that.getStartPoint())))
				&& ((this.getEndPoint() == null && that.getEndPoint() == null) || (this.getEndPoint().equals(that.getEndPoint())));
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
		result = result * 31 + getMap().hashCode();
		result = result * 31 + getStartPoint().hashCode();
		result = result * 31 + getEndPoint().hashCode();
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[tags:");
		builder.append(getMap());
		builder.append(", start:");
		builder.append(getStartPoint());
		builder.append(", end:");
		builder.append(getEndPoint());
		builder.append("]");
		return builder.toString();
	}
}
