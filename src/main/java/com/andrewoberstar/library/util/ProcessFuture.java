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

package com.andrewoberstar.library.util;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the future result of a <code>Process</code>. In addition to the
 * <code>Process</code>'s return code, it also provides access to it's stdout
 * and stderr streams.
 * @see Process, ProcessUtil, StreamDumper
 */
public class ProcessFuture implements Future<Integer> {
	private final Logger logger = LoggerFactory.getLogger(ProcessFuture.class);
	private Process proc;
	private Future<String> output;
	private Future<String> error;
	private boolean cancelled = false;
	private boolean done = false;
	
	/**
	 * Constructs a <code>ProcessFuture</code> from a given <code>Process</code>.
	 * @param proc the <code>Process</code> to associate with this object
	 */
	public ProcessFuture(Process proc) {
		this.proc = proc;
		ExecutorService threadPool = Executors.newFixedThreadPool(2);
		output = threadPool.submit(new StreamDumper(proc.getInputStream()));
		error = threadPool.submit(new StreamDumper(proc.getErrorStream()));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		try {
			proc.exitValue();
			logger.debug("Process could not be cancelled.  It is already complete.");
			return false;
		} catch (IllegalThreadStateException e) {
			if (mayInterruptIfRunning) {
				proc.destroy();
				done = true;
				cancelled = true;
				logger.debug("Process cancelled successfully.");
				return true;
			} else {
				logger.debug("Process not cancelled. Permission not given to interrupt.");
				return false;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDone() {
		if (isCancelled()) {
			return true;
		} else if (!done) {
			try {
				proc.exitValue();
				done = true;
				return true;
			} catch (IllegalThreadStateException e) {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer get() throws InterruptedException {
		if (isCancelled()) {
			throw new CancellationException("Process was cancelled");
		} else if (isDone()) {
			return proc.exitValue();
		} else {
			return proc.waitFor();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
		long millis = unit.toMillis(timeout);
		long wait = millis / 10;
		wait = wait < 100 ? 100 : wait;
		
		if (isCancelled()) {
			throw new CancellationException("Process was cancelled");
		} else {
			long start = new Date().getTime();
			long current = start;
			
			while (current - start < millis && !Thread.interrupted()) {
				try {
					return proc.exitValue();
				} catch (IllegalThreadStateException e) {
					Thread.sleep(wait);
					current = new Date().getTime();
				}
			}
			
			if (Thread.interrupted()) {
				throw new InterruptedException("This thread was interrupted.");
			} else {
				throw new TimeoutException("Process timed out.");
			}
		}
	}

	/**
	 * Waits if necessary for the computation to complete, and then retrieves its
	 * output.
	 * @return the <code>Process</code>'s stdout dumped into a <code>String</code>
	 * @throws InterruptedException - if the current thread was interrupted while waiting
	 * @throws ExecutionException - if the stdout handling threw an exception
	 */
	public String getOutput() throws InterruptedException, ExecutionException {
		if (isCancelled()) {
			throw new CancellationException("Process was cancelled.");
		} else if (output.isCancelled()) {
			throw new CancellationException("Process output handling was cancelled.");
		} else {
			return output.get();
		}
	}
	
	/**
	 * Waits if necessary for the computation to complete, and then retrieves its
	 * error.
	 * @return the <code>Process</code>'s stderr dumped into a <code>String</code>
	 * @throws InterruptedException - if the current thread was interrupted while waiting
	 * @throws ExecutionException - if the stderr handling threw an exception
	 */
	public String getError() throws InterruptedException, ExecutionException {
		if (isCancelled()) {
			throw new CancellationException("Process was cancelled.");
		} else if (error.isCancelled()) {
			throw new CancellationException("Process error handling was cancelled.");
		} else {
			return error.get();
		}
	}
	
	public static ProcessFuture execute(List<String> command) throws IOException {
		return new ProcessFuture(new ProcessBuilder(command).start());
	}
}
