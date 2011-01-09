/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mulima.meta;

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
		return Integer.valueOf(getFirst(GenericTag.DISC_NUMBER));
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
	
	/**
	 * Tidies the tags on this disc.
	 */
	public void tidy() {
		tidy(tracks);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (!(obj instanceof Disc))
			return false;
		
		Disc that = (Disc) obj;
		return this.getMap().equals(that.getMap())
			&& this.getTracks().equals(that.getTracks());
	}
	
	@Override
	public int hashCode() {
		return ("" + this.getMap().hashCode()
			+ this.getTracks().hashCode()).hashCode();
	}
	
	@Override
	public String toString() {
		return "[tags: " + this.getMap() + ", tracks: " + this.getTracks() + "]";
	}
}