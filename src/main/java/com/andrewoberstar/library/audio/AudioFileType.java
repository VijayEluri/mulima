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

package com.andrewoberstar.library.audio;

import java.io.File;

import org.springframework.util.StringUtils;

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
	
	public String getExtension() {
		return ext;
	}
	
	public boolean isOfType(File file) {
		String ext = StringUtils.getFilenameExtension(file.getAbsolutePath());
		return this.getExtension().equals(ext);
	}
	
	public static AudioFileType valueOf(File file) {
		String ext = StringUtils.getFilenameExtension(file.getAbsolutePath());
		for (AudioFileType type : AudioFileType.values()) {
			if (type.getExtension().equals(ext))
				return type;
		}
		throw new IllegalArgumentException("No type with extension \"" + ext + "\" exists.");
	}
}
