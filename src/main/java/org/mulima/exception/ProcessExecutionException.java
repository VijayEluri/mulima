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
package org.mulima.exception;

/**
 * Signals a process failure.
 */
public class ProcessExecutionException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an empty exception.
	 */
	public ProcessExecutionException() {
		super();
	}

	/**
	 * Constructs an exception from parameters.
	 * @param message the message
	 */
	public ProcessExecutionException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception from parameters.
	 * @param cause the cause
	 */
	public ProcessExecutionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs an exception from parameters.
	 * @param message the message
	 * @param cause the cause
	 */
	public ProcessExecutionException(String message, Throwable cause) {
		super(message, cause);
	}
}
