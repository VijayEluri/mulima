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
package org.mulima.util.io;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.mulima.exception.ProcessFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessCaller implements Callable<String> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String description;
	private final List<String> command;
	
	public ProcessCaller(String description, List<String> command) {
		this.description = description;
		this.command = command;
	}
	
	public ProcessCaller(String description, String... command) {
		this.description = description;
		this.command = Arrays.asList(command);
	}
	
	@Override
	public String call() throws Exception {
		logger.info("Starting: " + description);
		logger.debug("Executing command: " + command);
		ProcessFuture proc = new ProcessFuture(new ProcessBuilder(command).start());
		int exit = proc.get();
		if (exit > 0) {
			logger.error("Failed: " + description);
			logger.error("Stdout: " + proc.getOutput());
			logger.error("Stderr: " + proc.getError());
			throw new ProcessFailureException("Command failed: " + command);
		} else {
			logger.info("Success: " + description);
			return proc.getOutput();
		}
	}
}
