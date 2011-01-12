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
package org.mulima.audio.util;

import java.util.List;
import java.util.concurrent.Callable;

import org.mulima.audio.AudioFile;
import org.mulima.audio.CodecResult;
import org.mulima.util.io.ProcessCaller;
import org.mulima.util.io.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodecCaller implements Callable<CodecResult> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String description;
	private final AudioFile source;
	private final AudioFile dest;
	private final List<String> command;
	
	public CodecCaller(String description, AudioFile source, AudioFile dest, List<String> command) {
		this.description = description;
		this.source = source;
		this.dest = dest;
		this.command = command;
	}
	
	@Override
	public CodecResult call() throws Exception {
		logger.info("Starting: " + description);
		logger.debug("Executing command: " + command);
		ProcessResult procResult = new ProcessCaller(command).call();
		logger.info("Finished: " + description);
		return new CodecResult(source, dest, procResult);
	}
}
