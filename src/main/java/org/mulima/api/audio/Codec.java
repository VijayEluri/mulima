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

import java.util.concurrent.Callable;

import org.mulima.exception.ProcessExecutionException;

/**
 * A codec specifies operations for encoding and decoding an
 * audio file.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Codec {
	/**
	 * Execute an encode operation immediately.
	 * @param source the file to encode
	 * @param dest the destination for the encoded file
	 * @return a codec result
	 * @throws ProcessExecutionException if there is a problem encoding
	 */
	CodecResult encode(AudioFile source, AudioFile dest) throws ProcessExecutionException;
	
	/**
	 * Execute a decode operation immediately.
	 * @param source the file to decode
	 * @param dest the destination for the decoded file
	 * @return a codec result
	 * @throws ProcessExecutionException if there is a problem decoding
	 */
	CodecResult decode(AudioFile source, AudioFile dest) throws ProcessExecutionException;
	
	/**
	 * Prepare an encode operation for later execution.
	 * @param source the file to encode
	 * @param dest the destination for the encoded file
	 * @return a callable that will execute the operation
	 */
	Callable<CodecResult> encodeLater(AudioFile source, AudioFile dest);
	
	/**
	 * Prepare a decode operation for later execution.
	 * @param source the file to decode
	 * @param dest the destination for the decoded file
	 * @return a callable that will execute the operation
	 */
	Callable<CodecResult> decodeLater(AudioFile source, AudioFile dest);
}
