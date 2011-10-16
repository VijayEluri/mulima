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
package org.mulima.api.audio.action

import org.mulima.api.audio.file.AudioFile
import org.mulima.api.proc.ProcessResult

/**
 * Represents the result of a codec operation.  Provides access to the
 * process's exit value, the source and destination files.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
class CodecResult extends ProcessResult {
	/**
	 * The source of the operation
	 */
	final AudioFile source
	
	/**
	 * The destination for the operation
	 */
	final AudioFile dest
	
	/**
	 * Constructs a codec result from a process result.
	 * @param source the source of the codec operation
	 * @param dest the destination of the codec operation
	 * @param result the result of the codec process
	 */
	CodecResult(AudioFile source, AudioFile dest, ProcessResult result) {
		this(source, dest, result.command, result.exitVal, result.output, result.error)
	}
	
	/**
	 * Constructs a codec result from the parameters.
	 * @param source the source of the codec operation
	 * @param dest the destination of the codec operation
	 * @param command the command executed
	 * @param exitVal the exit value of the process
	 * @param output the std out of the process
	 * @param error the std err of the process
	 */
	CodecResult(AudioFile source, AudioFile dest, String command, int exitVal, String output, String error) {
		super(command, exitVal, output, error)
		this.source = source
		this.dest = dest
	}
}
