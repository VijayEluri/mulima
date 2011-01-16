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

import org.mulima.meta.Track;
import org.mulima.util.io.ProcessResult;

/**
 * Represents the result of a tagger operation.  Provides access to the
 * process's exit value, the file and metadata.
 */
public class TaggerResult extends ProcessResult {
	private final AudioFile file;
	private final Track track;
	
	public TaggerResult(AudioFile file, Track track, ProcessResult result) {
		this(file, track, result.getExitVal(), result.getOutput(), result.getError());
	}
	
	public TaggerResult(AudioFile file, Track track, int exitVal, String output, String error) {
		super(exitVal, output, error);
		this.file = file;
		this.track = track;
	}

	/**
	 * @return the file
	 */
	public AudioFile getFile() {
		return file;
	}

	/**
	 * @return the track
	 */
	public Track getTrack() {
		return track;
	}

}
