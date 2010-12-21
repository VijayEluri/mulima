/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
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

package com.andrewoberstar.library.audio.impl;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.exception.CodecFailureException;
import com.andrewoberstar.library.util.ProcessFuture;

public class CodecCaller implements Callable<AudioFile> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String name;
	private final List<String> command;
	private final AudioFile source;
	private final AudioFile dest;
	
	public CodecCaller(String name, List<String> command, AudioFile source, AudioFile dest) {
		this.name = name;
		this.command = command;
		this.source = source;
		this.dest = dest;
	}
	
	@Override
	public AudioFile call() throws Exception {
		logger.info("Starting: " + name);
		logger.debug("Executing command: " + command);
		ProcessFuture proc = new ProcessFuture(new ProcessBuilder(command).start());
		int exit = proc.get();
		if (exit > 0) {
			logger.error("Failed: " + name);
			logger.error("Stdout: " + proc.getOutput());
			logger.error("Stderr: " + proc.getError());
			throw new CodecFailureException("Coding failed for source (" + source.getFile().getName() + ") to dest (" + dest.getFile().getName() + ").");
		} else {
			logger.info("Success: " + name);
			return dest;
		}
	}
}
