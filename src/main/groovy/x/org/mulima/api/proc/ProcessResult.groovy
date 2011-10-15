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
package x.org.mulima.api.proc

import org.mulima.util.StringUtil

/**
 * Represents the result of a Process execution.  Provides
 * access to the exit value as well as the standard out
 * and standard error output.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
class ProcessResult {
	/**
	 * The command that was executed
	 */
	final String command
	
	/**
	 * The exit value of the process
	 */
	final int exitVal
	
	/**
	 * The system output of the process
	 */
	final String output
	
	/**
	 * The system error output of the process
	 */
	final String error
	
	ProcessResult(List<String> command, int exitVal, String output, String error) {
		this(StringUtil.join(command, " "), exitVal, output, error)
	}
	
	ProcessResult(String command, int exitVal, String output, String error) {
		this.command = command
		this.exitVal = exitVal
		this.output = output
		this.error = error
	}
	
	/**
	 * @return true if the process was successful (exit value 
	 * of 0), false otherwise
	 */
	boolean isSuccess() {
		return exitVal == 0
	}
}
