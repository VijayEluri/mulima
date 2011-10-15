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
package z.org.mulima.api.proc

import java.util.concurrent.Callable

import org.mulima.exception.ProcessExecutionException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Executes a <code>Process</code>.  This is an alternative to {@link ProcessBuilder#start()}
 * and {@link Runtime#exec(String)} that will give you a {@link ProcessResult} object.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
class ProcessCaller implements Callable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessCaller.class)
	private final List command
	
	/**
	 * Constructs a process caller with the specified operating system program and arguments.
	 * @param command the list containing the program and its arguments.
	 */
	ProcessCaller(List command) {
		this.command = command
	}
	
	/**
	 * Constructs a process caller with the specified operating system program and arguments.
	 * @param command a string array containing the program and its arguments.
	 */
	ProcessCaller(String... command) {
		this.command = command
	}
	
	/**
	 * Starts a process using the command specified in the constructor.
	 * @return a process result holding the output of the process.
	 * @throws ProcessExecutionException if there is a problem with the process
	 */
	@Override
	ProcessResult call() {
		LOGGER.debug("Executing command: " + command)
		Process proc
		try {
			proc = new ProcessBuilder(command).start()
		} catch (IOException e) {
			throw new ProcessExecutionException(e)
		}
		
		StringBuilder output = new StringBuilder()
		StringBuilder error = new StringBuilder()
		proc.waitForProcessOutput(output, error)
		int exit = proc.exitValue()
		return new ProcessResult(command, exit, output, error)
	}
}
