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
package org.mulima.audio;

import java.io.File;
import java.net.URI;

/**
 * 
 */
public class AudioFile extends File {
	private static final long serialVersionUID = 1L;

	public AudioFile(File parent, String child) {
		super(parent, child);
	}

	public AudioFile(String parent, String child) {
		super(parent, child);
	}

	public AudioFile(String pathname) {
		super(pathname);
	}

	public AudioFile(URI uri) {
		super(uri);
	}

	public AudioFileType getType() {
		return AudioFileType.valueOf(this);
	}
}