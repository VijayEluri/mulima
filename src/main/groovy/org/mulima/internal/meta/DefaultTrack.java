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
		int thisDisc = getDiscNum();
		int otherDisc = o.getDiscNum();
		
		if (thisDisc == otherDisc) {
			if (getNum() == o.getNum()) {
				return 0;
			} else {
				return getNum() < o.getNum() ? -1 : 1;
			}
		} else {
			return thisDisc < otherDisc ? -1 : 1;
		}
	}
}
