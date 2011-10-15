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

import java.util.concurrent.Callable;

import z.org.mulima.api.file.AudioFile;

/**
 * A tagger specifies operations to read and write metadata
 * from an audio file.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Tagger {
	/**
	 * Executes a write operation immediately.
	 * @param file the file to write to
	 * @return a tagger result
	 */
	TaggerResult write(AudioFile file);
	
	/**
	 * Executes a read operation immediately.
	 * @param file the file to read from
	 * @return a tagger result
	 */
	TaggerResult read(AudioFile file);
	
	/**
	 * Preapres a write operation for later execution.
	 * @param file the file to write to
	 * @return a callable that will execute the tag
	 */
	Callable<TaggerResult> writeLater(AudioFile file);
	
	/**
	 * Prepares a read operation for later execution.
	 * @param file the file to read from
	 * @return a callable that will execute the tag
	 */
	Callable<TaggerResult> readLater(AudioFile file);
}
