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


/**
 * Holds metadata associated with a track on an <code>Album</code>.
 * @see Album, Disc
 */
public class Track extends AbstractMetadata {
	/**
	 * Gets the value of {@link GenericTag#TRACK_NUMBER}.
	 * @return the number of the track
	 */
	public int getNum() {
		return Integer.valueOf(getFirst(GenericTag.TRACK_NUMBER));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (!(obj instanceof Track)) {
			return false;
		}
		
		Track that = (Track) obj;
		return this.getMap().equals(that.getMap());
	}
	
	@Override
	public int hashCode() {
		return getMap().hashCode();
	}
	
	@Override
	public String toString() {
		return "[tags: " + getMap().toString() + "]";
	}
}
