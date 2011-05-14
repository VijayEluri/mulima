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
package org.mulima.api.meta;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents an album.  This includes the metadata, discs,
 * and cue sheets.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class Album extends AbstractMetadata implements Metadata {
	private final SortedSet<CueSheet> cueSheets = new TreeSet<CueSheet>();
	private final SortedSet<Disc> discs = new TreeSet<Disc>();
	
	/**
	 * Gets the cue sheets that go with this
	 * album.  This should never return null.
	 * @return this album's cues
	 */
	SortedSet<CueSheet> getCueSheets() {
		return cueSheets;
	}
	
	/**
	 * Gets the discs that are part of this
	 * album.  This should never return null.
	 * @return this album's discs
	 */
	SortedSet<Disc> getDiscs() {
		return discs;
	}
	
	/**
	 * Creates a list of this album's tracks
	 * with all of the metadata from their parent
	 * disc and album. 
	 * @return a list of the tracks with all metadata
	 */
	SortedSet<Track> flatten() {
		SortedSet<Track> tracks = new TreeSet<Track>();
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
	 * Simplify the metadata for this album, by moving all
	 * metadata that is common among all tracks to the disc
	 * or album (if common between discs).
	 */
	void tidy() {
		tidy(getDiscs());
	}
}
