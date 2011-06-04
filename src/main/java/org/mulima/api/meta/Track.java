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

/**
 * Represents an album track.  This includes the
 * track's metadata.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class Track extends AbstractMetadata implements Metadata, Comparable<Track> {
	private final CuePoint startPoint;
	private final CuePoint endPoint;
	
	public Track() {
		this(null, null);
	}
	
	public Track(CuePoint startPoint, CuePoint endPoint) {
		super();
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}
	
	/**
	 * Gets the value of {@link GenericTag#TRACK_NUMBER}.
	 * @return the number of the track
	 */
	public int getNum() {
		return Integer.valueOf(getFirst(GenericTag.TRACK_NUMBER));
	}
	
	/**
	 * Gets the cue point for the start of this
	 * track.  This may be {@code null}.
	 * @return the starting cue point
	 */
	public CuePoint getStartPoint() {
		return startPoint;
	}
	
	/**
	 * Gets the cue point for the end of this
	 * track.  This may be {@code null}.
	 * @return the ending cue point
	 */
	public CuePoint getEndPoint() {
		return endPoint;
	}

	/**
	 * Compares to another track by num.
	 * @param o other track
	 */
	@Override
	public int compareTo(Track o) {
		if (getNum() == o.getNum()) {
			return 0;
		} else {
			return getNum() < o.getNum() ? -1 : 1;
		}
	}
}
