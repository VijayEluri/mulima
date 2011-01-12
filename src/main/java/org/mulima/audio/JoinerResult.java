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

import org.mulima.meta.CueSheet;
import org.mulima.util.io.ProcessResult;

/**
 * Represents the result of a joiner operation.  Provides access to the
 * process's exit value, the source and destination files.
 */
public class JoinerResult extends ProcessResult {
	private final AudioFile dest;
	private final List<AudioFile> source;
	private final CueSheet cue;
	
	public JoinerResult(List<AudioFile> source, AudioFile dest, CueSheet cue, ProcessResult result) {
		this(source, dest, cue, result.getExitVal(), result.getOutput(), result.getError());
	}
	
	public JoinerResult(List<AudioFile> source, AudioFile dest, CueSheet cue, int exitVal, String output, String error) {
		super(exitVal, output, error);
		this.dest = dest;
		this.source = source;
		this.cue = cue;
	}
	
	/**
	 * @return the source
	 */
	public List<AudioFile> getSource() {
		return source;
	}
	
	/**
	 * @return the dest
	 */
	public AudioFile getDest() {
		return dest;
	}

	/**
	 * @return the cue
	 */
	public CueSheet getCue() {
		return cue;
	}
}
