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

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a cue sheet from a CD.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class CueSheet extends AbstractMetadata implements Metadata {
	private final File file;
	private final int num;
	private final SortedSet<CuePoint> cuePoints = new TreeSet<CuePoint>();
	
	/**
	 * Creates a CueSheet that was not sourced from
	 * a file.
	 * @param num the number of the cue (usually disc number)
	 */
	public CueSheet(int num) {
		this(num, null);
	}
	
	/**
	 * Creates a CueSheet that was sourced from
	 * the given file.
	 * @param num the number of the cue (usually disc number)
	 * @param file the file this cue sheet was sourced from
	 */
	public CueSheet(int num, File file) {
		this.num = num;
		this.file = file;
	}
	
	/**
	 * Gets the file that this CueSheet was
	 * parsed from.
	 * @return the source file
	 */
	File getFile() {
		return file;
	}
	
	/**
	 * Gets the number of this cue sheet.
	 * This will correspond to the disc
	 * number.
	 * @return the number
	 */
	int getNum() {
		return num;
	}
	
	/**
	 * Gets the cue points for this sheet.
	 * This only includes track start points. 
	 * @return set of cue points
	 */
	SortedSet<CuePoint> getCuePoints() {
		//TODO only include the index 1 points
		return cuePoints;
	}
	
	/**
	 * Gets all cue points for this sheet.
	 * This includes all indices.
	 * @return set of all cue points
	 */
	SortedSet<CuePoint> getAllCuePoints() {
		return cuePoints;
	}
}
