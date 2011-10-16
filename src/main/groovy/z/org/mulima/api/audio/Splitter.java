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
package z.org.mulima.api.audio;

import java.io.File;
import java.util.concurrent.Callable;

import z.org.mulima.api.file.DiscFile;

/**
 * A splitter specifies operations for splitting an audio file
 * as specified in a cue sheet.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Splitter {
	/**
	 * Executes a split operation immediately.
	 * @param image the file to split
	 * @param destDir the destination directory for the files
	 * @return a splitter result
	 */
	SplitterResult split(DiscFile image, File destDir);
	
	/**
	 * Prepares a split operation for later execution.
	 * @param image the file to split
	 * @param destDir the destination directory for the files
	 * @return a callable that will execute the split
	 */
	Callable<SplitterResult> splitLater(DiscFile image, File destDir);
}