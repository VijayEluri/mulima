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

import org.springframework.util.StringUtils;

/**
 * Enumerates the types of audio files.
 */
public enum AudioFileType {
	WAVE("wav"),
	FLAC("flac"),
	VORBIS("ogg"),
	AAC("m4a"),
	MP3("mp3");
	
	private final String ext;
	
	private AudioFileType(String ext) {
		this.ext = ext;
	}

	/**
	 * Gets the file extension used for this type.
	 * @return file extension
	 */
	public String getExtension() {
		return ext;
	}
	
	/**
	 * Tests a file to see if it is of the
	 * same type.  Uses the file extension.
	 * @param file file to test.
	 * @return <code>true</code> if of the same type, <code>false</code> otherwise
	 */
	public boolean isOfType(File file) {
		String ext = StringUtils.getFilenameExtension(file.getAbsolutePath());
		return this.getExtension().equals(ext);
	}
	
	/**
	 * Gets the file type of a given file.
	 * @param file the file to get the type of
	 * @return the type of the file
	 */
	public static AudioFileType valueOf(File file) {
		String ext = StringUtils.getFilenameExtension(file.getAbsolutePath());
		for (AudioFileType type : AudioFileType.values()) {
			if (type.getExtension().equals(ext))
				return type;
		}
		throw new IllegalArgumentException("No type with extension \"" + ext + "\" exists.");
	}
}
