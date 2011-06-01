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

import java.util.List;

import org.mulima.api.meta.CueSheet;
import org.mulima.proc.ProcessResult;

/**
 * Represents the result of a joiner operation.  Provides access to the
 * process's exit value, the source and destination files.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class JoinerResult extends ProcessResult {
	private final AudioFile dest;
	private final List<AudioFile> source;
	private final CueSheet cue;
	
	/**
	 * Constructs a joiner result from a process result.
	 * @param source the source files of the join operation
	 * @param dest the destination of the join operation
	 * @param cue the cue file resulting from the join
	 * @param result the result of the join process
	 */
	public JoinerResult(List<AudioFile> source, AudioFile dest, CueSheet cue, ProcessResult result) {
		this(source, dest, cue, result.getCommand(), result.getExitVal(),
			result.getOutput(), result.getError());
	}
	
	/**
	 * Constructs a joiner result from the parameters.
	 * @param source the source files of the join operation
	 * @param dest the destination of the join operation
	 * @param cue the cue file resulting from the join
	 * @param command the command executed
	 * @param exitVal the exit value of the process
	 * @param output the std out of the process
	 * @param error the std err of the process
	 */
	public JoinerResult(List<AudioFile> source, AudioFile dest, CueSheet cue, String command, int exitVal,
		String output, String error) {
		super(command, exitVal, output, error);
		this.dest = dest;
		this.source = source;
		this.cue = cue;
	}
	
	/**
	 * Gets the source files.
	 * @return the source
	 */
	public List<AudioFile> getSource() {
		return source;
	}
	
	/**
	 * Gets the destination file.
	 * @return the dest
	 */
	public AudioFile getDest() {
		return dest;
	}

	/**
	 * Gets the resulting cue sheet.
	 * @return the cue
	 */
	public CueSheet getCue() {
		return cue;
	}
}
