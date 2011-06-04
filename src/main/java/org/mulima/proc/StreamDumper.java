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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dumps an <code>InputStream</code> to a <code>String</code>. The 
 * stream will be handled in a separate thread.  A common use for 
 * this would be to handle <code>Process</code> output.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class StreamDumper implements Callable<String> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private InputStream in;
	private StringBuilder buffer;
	
	/**
	 * Constructs a <code>StreamDumper</code> without an associated
	 * <code>InputStream</code>.  Must use {@link #setInputStream(InputStream)}
	 * before running {@link #call()}.
	 */
	public StreamDumper() {
		this.buffer = new StringBuilder();
	}
	
	/**
	 * Constructs a <code>StreamDumper</code> with an associated
	 * <code>InputStream</code>.
	 * @param in the <code>InputStream</code> to dump
	 */
	public StreamDumper(InputStream in) {
		this.in = in;
		this.buffer = new StringBuilder();
	}
	
	/**
	 * Sets the <code>InputStream</code> to dump.
	 * @param is the <code>InputStream</code> to dump
	 */
	public void setInputStream(InputStream is) {
		this.in = is;
	}

	/**
	 * Dumps the contents of the <code>InputStream</code>
	 * and returns them as a <code>String</code>.
	 * @return a <code>String</code> of the contents of
	 * the <code>InputStream</code>
	 */
	@Override
	public String call() throws IOException {
		logger.trace("Entering call method");
		BufferedReader input = new BufferedReader(new InputStreamReader(in));
		String line = input.readLine();
		while (line != null && !Thread.interrupted()) {
			buffer.append(line + "\n");
			line = input.readLine();
		}
		if (Thread.interrupted()) {
			logger.debug("Thread was interrupted");
		}
		input.close();
		logger.trace("Exiting call method");
		return buffer.toString();
	}
}
