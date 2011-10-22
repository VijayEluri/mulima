package org.mulima.internal.meta;

import java.util.SortedSet;
import java.util.TreeSet;

import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Track;

/**
 * Default implementation of a disc.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultDisc extends AbstractMetadata implements Disc {
	private final SortedSet<Track> tracks = new TreeSet<Track>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNum() {
		return Integer.valueOf(getFirst(GenericTag.DISC_NUMBER));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<Track> getTracks() {
		return tracks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Track getTrack(int num) {
		for (Track track : tracks) {
			if (track.getNum() == num) {
				return track;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tidy() {
		tidy(getTracks());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Disc o) {
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
		} else if (obj instanceof DefaultDisc) {
			DefaultDisc that = (DefaultDisc) obj;
			return this.getMap().equals(that.getMap()) && this.getTracks().equals(that.getTracks());
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
		result = result * 31 + getTracks().hashCode();
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
		builder.append(", tracks:");
		builder.append(getTracks());
		builder.append("]");
		return builder.toString();
	}
}
