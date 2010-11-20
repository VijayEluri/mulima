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

package com.andrewoberstar.library.exception;

public class CodecFailureException extends KnownCodecException {
	private static final long serialVersionUID = 1L;

	public CodecFailureException() {
		super();
	}

	public CodecFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodecFailureException(String message) {
		super(message);
	}

	public CodecFailureException(Throwable cause) {
		super(cause);
	}

}
