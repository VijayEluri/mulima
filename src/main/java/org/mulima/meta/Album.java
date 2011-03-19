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
 * Represents an album's metadata.  Contains discs and cue sheets.
 */
public class Album extends AbstractMetadata {
	private List<CueSheet> cues = new ArrayList<CueSheet>();
	private List<Disc> discs = new ArrayList<Disc>();
	
	/**
	 * @return the cues
	 */
	public List<CueSheet> getCues() {
		return cues;
	}

	/**
	 * @param cues the cues to set
	 */
	public void setCues(List<CueSheet> cues) {
		this.cues = cues;
	}

	/**
	 * @return the discs
	 */
	public List<Disc> getDiscs() {
		return discs;
	}

	/**
	 * @param discs the discs to set
	 */
	public void setDiscs(List<Disc> discs) {
		this.discs = discs;
	}
	
	/**
	 * @return a list of all tracks for this album.
	 */
	public List<Track> flat() {
		List<Track> tracks = new ArrayList<Track>();
		for (Disc disc : discs) {
			for (Track track : disc.getTracks()) {
				Track temp = new Track();
				for (Tag tag : GenericTag.values()) {
					if (track.isSet(tag)) {
						temp.add(tag, track.getAll(tag));
					} else if (disc.isSet(tag)) {
						temp.add(tag, disc.getAll(tag));
					} else if (this.isSet(tag)) {
						temp.add(tag, this.getAll(tag));
					}
				}
				tracks.add(temp);
			}
		}
		return tracks;
	}
	
	/**
	 * Tidies the tags on this album.
	 */
	public void tidy() {
		for (Disc disc : discs) {
			disc.tidy();
		}
		tidy(discs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (!(obj instanceof Album)) {
			return false;
		}
		
		Album that = (Album) obj;
		return this.getMap().equals(that.getMap())
			&& this.getCues().equals(that.getCues())
			&& this.getDiscs().equals(that.getDiscs());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return ("" + getMap().hashCode()
			+ getCues().hashCode()
			+ getDiscs().hashCode()).hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[tags: " + getMap() + ", cues: " + getCues() + ", discs: " + getDiscs() + "]";
	}
}
