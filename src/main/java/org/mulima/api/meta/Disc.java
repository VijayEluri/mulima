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
 * Represents an album disc.  This includes
 * metadata and tracks.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class Disc extends AbstractMetadata implements Metadata {
	private final SortedSet<Track> tracks = new TreeSet<Track>();
	
	/**
	 * Gets the value of {@link GenericTag#DISC_NUMBER}.
	 * @return the number of the disc
	 */
	public int getNum() {
		return Integer.valueOf(getFirst(GenericTag.DISC_NUMBER));
	}
	
	/**
	 * Gets the tracks that are on this
	 * disc.
	 * @return this disc's tracks
	 */
	public SortedSet<Track> getTracks() {
		return tracks;
	}
	
	/**
	 * Simplify the metadata for this disc, by moving all
	 * metadata that is common among all tracks to this disc.
	 */
	public void tidy() {
		tidy(getTracks());
	}
}
