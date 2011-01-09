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
package org.mulima.util;

/**
 * Tracks the progress of an operation.
 */
public interface ProgressBar {
	/**
	 * Notify the <code>ProgressBar</code> that another operation has completed.
	 * @return the current number of complete operations
	 */
	public int next();
	
	/**
	 * Notify the <code>ProgressBar</code> that all operations have completed.
	 * This is handy to ensure that any completion events occur.
	 */
	public void done();
}
