package com.andrewoberstar.library.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds metadata associated with a disc on an <code>Album</code>.
 * @see Album, Track
 */
public class Disc extends AbstractMetadata {
	private List<Track> tracks = new ArrayList<Track>();
	
	/**
	 * Gets the value of {@link GenericTag#DISC_NUMBER}.
	 * @return the number of the disc
	 */
	public int getNum() {
		return Integer.valueOf(getTags().getFirst(GenericTag.DISC_NUMBER));
	}

	/**
	 * Gets the tracks associated with this <code>Disc</code>.
	 * @return the tracks for this disc
	 */
	public List<Track> getTracks() {
		return tracks;
	}

	/**
	 * Sets the tracks associated with this <code>Disc</code>.
	 * @param tracks the tracks to set for this disc
	 */
	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (!(obj instanceof Disc))
			return false;
		
		Disc that = (Disc) obj;
		return this.getTags().equals(that.getTags())
			&& this.getTracks().equals(that.getTracks());
	}
	
	@Override
	public int hashCode() {
		return ("" + this.getTags().hashCode()
			+ this.getTracks().hashCode()).hashCode();
	}
	
	@Override
	public String toString() {
		return "[tags: " + this.getTags() + ", tracks: " + this.getTracks() + "]";
	}
}
