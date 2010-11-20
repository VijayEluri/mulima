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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs the progress of an operation using SLF4J INFO level messages.
 */
public class ProgressBar {
	private final Logger logger = LoggerFactory.getLogger(ProgressBar.class);
	private String name;
	private int total;
	private int count = 0;
	private double ratio;
	private int percent = 0;

	/**
	 * Constructs a <code>ProgressBar</code> named <code>name</code>
	 * with <code>total</code> operations to complete.
	 * @param name the name of the operation.  This will be used to prefix the
	 * log messages.
	 * @param total the total number of operations to complete for this <code>ProgressBar</code>
	 */
	public ProgressBar(String name, int total) {
		this.name = name;
		this.total = total;
		ratio = (double) total / 100;
		logger.info(name + ": " + percent + "% complete (" + count + " of " + total + ")");
	}

	/**
	 * Notify the <code>ProgressBar</code> that another operation has completed.
	 * This will print a log message if a multiple of 10% of the operations
	 * are complete.
	 * @return the current number of complete operations
	 */
	public synchronized int next()	{
		count++;
		if (count > total) {
			return count;
		} else if (count % ratio < 1) {
			percent++;
			if (percent % 10 == 0) {
				logger.info(name + ": " + percent + "% complete (" + count + " of " + total + ")");
			}
		}
		return count;
	}
	
	/**
	 * Notify the <code>ProgressBar</code> that all operations have completed.
	 * This is handy to ensure that the completion log message is printed.
	 */
	public synchronized void done() {
		count = total;
		logger.info(name + ": " + percent + "% complete (" + count + " of " + total + ")");
	}
}
