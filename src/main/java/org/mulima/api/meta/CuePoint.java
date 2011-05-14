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

import java.util.regex.Pattern;

/**
 * Represents an index point from 
 * a {@link CueSheet}.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class CuePoint {
	private static final Pattern TIME_REGEX = Pattern.compile("^[0-9]{2}:[0-9]{2}.[0-9]{3}$");
	
	private final int track;
	private final int index;
	private final String time;
	
	/**
	 * Creates a cue point from the arguments.
	 * @param track the track number for this point (must be greater than 0)
	 * @param index the index number for this point (must be 0 or greater)
	 * @param time the time of this point (must be in MM:SS:FF format)
	 * @throws IllegalArgumentException if any arguments don't meet the constraints
	 * above
	 */
	public CuePoint(int track, int index, String time) {
		if (track > 0) {
			this.track = track;
		} else {
			throw new IllegalArgumentException("Track number must be greater than 0.");
		}
		
		if (index >= 0) {
			this.index = index;
		} else {
			throw new IllegalArgumentException("Index number must be 0 or greater.");
		}
		
		if (verifyTimeFormat(time)) {
			this.time = time;	
		} else {
			throw new IllegalArgumentException("Time must match the following format: " + TIME_REGEX.pattern());
		}
	}
	
	/**
	 * Verifies that the time argument is in the correct format.
	 * @param time the time of this point
	 * @return {@code true} if it is in the correct format, {@code false} otherwise
	 */
	private boolean verifyTimeFormat(String time) {
		return TIME_REGEX.matcher(time).matches();
	}
	
	/**
	 * Gets the track number for this point.
	 * @return track number
	 */
	public int getTrack() {
		return track;
	}
	
	/**
	 * Gets the index number for this point.
	 * @return index number
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Gets the time that this point represents.
	 * @return the cue time
	 */
	public String getTime() {
		return time;
	}
}
