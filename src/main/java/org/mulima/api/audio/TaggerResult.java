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
package org.mulima.api.audio;

import org.mulima.api.meta.Track;
import org.mulima.proc.ProcessResult;

/**
 * Represents the result of a tagger operation.  Provides access to the
 * process's exit value, the file and metadata.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class TaggerResult extends ProcessResult {
	private final AudioFile file;
	private final Track track;
	
	/**
	 * Constructs a tagger result from a process result.
	 * @param file the file read from or written to
	 * @param track the track metadata that was written or read
	 * @param result the result of the tagger process
	 */
	public TaggerResult(AudioFile file, Track track, ProcessResult result) {
		this(file, track, result.getCommand(), result.getExitVal(), result.getOutput(), result.getError());
	}
	
	/**
	 * Constructs a tagger result from the parameters.
	 * @param file the file read from or written to
	 * @param track the track metadata that was written or read
	 * @param command the command executed
	 * @param exitVal the exit value of the process
	 * @param output the std out of the process
	 * @param error the std err of the process
	 */
	public TaggerResult(AudioFile file, Track track, String command, int exitVal, String output, String error) {
		super(command, exitVal, output, error);
		this.file = file;
		this.track = track;
	}

	/**
	 * Gets the file read from or written to.
	 * @return the file
	 */
	public AudioFile getFile() {
		return file;
	}

	/**
	 * Gets the metadata read or written.
	 * @return the track
	 */
	public Track getTrack() {
		return track;
	}

}
