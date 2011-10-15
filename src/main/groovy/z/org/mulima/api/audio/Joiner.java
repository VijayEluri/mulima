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

import java.util.List;
import java.util.concurrent.Callable;

import z.org.mulima.api.file.AudioFile;

/**
 * A joiner specifies operations for joining audio files into
 * a single file.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Joiner {
	/**
	 * Execute a join operation immediately.
	 * @param files the files to join
	 * @param dest the destination file
	 * @return a joiner result
	 */
	JoinerResult join(List<AudioFile> files, AudioFile dest);
	
	/**
	 * Prepared a join operation for later execution.
	 * @param files the files to join
	 * @param dest the destination file
	 * @return a callable that will execute the join
	 */
	Callable<JoinerResult> joinLater(List<AudioFile> files, AudioFile dest);
}
