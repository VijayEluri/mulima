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

import java.util.concurrent.Callable;

import org.mulima.meta.Track;

/**
 * A tagger specifies operations to read and write metadata
 * from an audio file.
 */
public interface Tagger {
	/**
	 * Executes a write operation immediately.
	 * @param file the file to write to
	 * @param track the track metadata to write
	 * @return a tagger result
	 * @throws Exception if there is a problem tagging
	 */
	TaggerResult write(AudioFile file, Track track) throws Exception;
	
	/**
	 * Executes a read operation immediately.
	 * @param file the file to read from
	 * @return a tagger result
	 * @throws Exception if there is a problem reading
	 */
	TaggerResult read(AudioFile file) throws Exception;
	
	/**
	 * Preapres a write operation for later execution.
	 * @param file the file to write to
	 * @param track the track metadata to write
	 * @return a callable that will execute the tag
	 */
	Callable<TaggerResult> writeLater(AudioFile file, Track track);
	
	/**
	 * Prepares a read operation for later execution.
	 * @param file the file to read from
	 * @return a callable that will execute the tag
	 */
	Callable<TaggerResult> readLater(AudioFile file);
}
