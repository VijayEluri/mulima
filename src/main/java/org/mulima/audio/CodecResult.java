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

import org.mulima.util.io.ProcessResult;

/**
 * Represents the result of a codec operation.  Provides access to the
 * process's exit value, the source and destination files.
 */
public class CodecResult extends ProcessResult {
	private final AudioFile source;
	private final AudioFile dest;
	
	public CodecResult(AudioFile source, AudioFile dest, ProcessResult result) {
		this(source, dest, result.getExitVal(), result.getOutput(), result.getError());
	}
	
	public CodecResult(AudioFile source, AudioFile dest, int exitVal, String output, String error) {
		super(exitVal, output, error);
		this.source = source;
		this.dest = dest;
	}
	
	/**
	 * @return the source
	 */
	public AudioFile getSource() {
		return source;
	}
	
	/**
	 * @return the dest
	 */
	public AudioFile getDest() {
		return dest;
	}
}
