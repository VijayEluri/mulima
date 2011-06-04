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
package org.mulima.proc;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mulima.exception.ProcessExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes a <code>Process</code>.  This is an alternative to {@link ProcessBuilder#start()}
 * and {@link Runtime#exec(String)} that will give you a {@link ProcessResult} object.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class ProcessCaller implements Callable<ProcessResult> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final List<String> command;
	
	/**
	 * Constructs a process caller with the specified operating system program and arguments.
	 * @param command The list containing the program and its arguments.
	 */
	public ProcessCaller(List<String> command) {
		this.command = command;
	}
	
	/**
	 * Constructs a process caller with the specified operating system program and arguments.
	 * @param command A string array containing the program and its arguments.
	 */
	public ProcessCaller(String... command) {
		this.command = Arrays.asList(command);
	}
	
	/**
	 * Starts a process using the command specified in the constructor.
	 * @return a process result holding the output of the process.
	 * @throws ProcessExecutionException if there is a problem with the process
	 */
	@Override
	public ProcessResult call() throws ProcessExecutionException {
		logger.debug("Executing command: " + command);
		Process proc;
		try {
			proc = new ProcessBuilder(command).start();
		} catch (IOException e) {
			throw new ProcessExecutionException(e);
		}
		
		ExecutorService threadPool = Executors.newFixedThreadPool(2);
		Future<String> output = threadPool.submit(new StreamDumper(proc.getInputStream()));
		Future<String> error = threadPool.submit(new StreamDumper(proc.getErrorStream()));
		
		try {
			int exit = proc.waitFor();
			String out = output.get();
			String err = error.get();
			return new ProcessResult(command, exit, out, err);
		} catch (InterruptedException e) {
			throw new ProcessExecutionException(e);
		} catch (ExecutionException e) {
			throw new ProcessExecutionException(e);
		}
	}
}
