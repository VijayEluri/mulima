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
package org.mulima.audio;

import java.io.File;
import java.util.concurrent.Callable;

import org.mulima.meta.CueSheet;

/**
 * A splitter specifies operations for splitting an audio file
 * as specified in a cue sheet.
 */
public interface Splitter {
	/**
	 * Executes a split operation immediately.
	 * @param image the file to split
	 * @param cue the cue sheet containing split points
	 * @param destDir the destination directory for the files
	 * @return a splitter result
	 * @throws Exception if there is a problem splitting the file
	 */
	SplitterResult split(AudioFile image, CueSheet cue, File destDir) throws Exception;
	
	/**
	 * Prepares a split operation for later execution.
	 * @param image the file to split
	 * @param cue the cue sheet containing split points
	 * @param destDir the destination directory for the files
	 * @return a callable that will execute the split
	 */
	Callable<SplitterResult> splitLater(AudioFile image, CueSheet cue, File destDir);
}
