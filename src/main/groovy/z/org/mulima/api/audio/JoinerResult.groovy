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
package z.org.mulima.api.audio

import z.org.mulima.api.file.AudioFile
import z.org.mulima.api.meta.CueSheet
import z.org.mulima.api.proc.ProcessResult

/**
 * Represents the result of a joiner operation.  Provides access to the
 * process's exit value, the source and destination files.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
class JoinerResult extends ProcessResult {
	/**
	 * The source files
	 */
	final List<AudioFile> source
	
	/**
	 * The destination file
	 */
	final AudioFile dest
	
	/**
	 * The resulting cue sheet
	 */
	final CueSheet cue
	
	/**
	 * Constructs a joiner result from a process result.
	 * @param source the source files of the join operation
	 * @param dest the destination of the join operation
	 * @param cue the cue file resulting from the join
	 * @param result the result of the join process
	 */
	JoinerResult(List<AudioFile> source, AudioFile dest, CueSheet cue, ProcessResult result) {
		this(source, dest, cue, result.command, result.exitVal, result.output, result.error)
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
	JoinerResult(List<AudioFile> source, AudioFile dest, CueSheet cue, String command, int exitVal,
		String output, String error) {
		super(command, exitVal, output, error)
		this.dest = dest
		this.source = source
		this.cue = cue
	}
}
