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

import java.util.List;

import org.mulima.proc.ProcessResult;

/**
 * Represents the result of a splitter operation.  Provides access to the
 * process's exit value, the source and destination files.
 */
public class SplitterResult extends ProcessResult {
	private final AudioFile source;
	private final List<AudioFile> dest;
	
	/**
	 * Constructs a splitter result from a process result.
	 * @param source the source of the split operation
	 * @param dest the destination files of the split operation
	 * @param result the result of the split process
	 */
	public SplitterResult(AudioFile source, List<AudioFile> dest, ProcessResult result) {
		this(source, dest, result.getCommand(), result.getExitVal(), result.getOutput(), result.getError());
	}
	
	/**
	 * Constructs a splitter result from the parameters.
	 * @param source the source of the split operation
	 * @param dest the destination files of the split operation
	 * @param command the command executed
	 * @param exitVal the exit value of the process
	 * @param output the std out of the process
	 * @param error the std err of the process
	 */
	public SplitterResult(AudioFile source, List<AudioFile> dest, String command, int exitVal, String output,
		String error) {
		super(command, exitVal, output, error);
		this.source = source;
		this.dest = dest;
	}
	
	/**
	 * Gets the source file.
	 * @return the source
	 */
	public AudioFile getSource() {
		return source;
	}
	
	/**
	 * Gets the destination files.
	 * @return the dest
	 */
	public List<AudioFile> getDest() {
		return dest;
	}
}
